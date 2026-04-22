import React, { useState, useEffect } from 'react'
import { X, Globe, Award, BookOpen, ChevronRight, User, ExternalLink, Upload, CheckCircle2, BadgeCheck } from 'lucide-react'
import { auth, db, storage } from '../config/firebase'
import { 
  signInWithPopup, 
  GoogleAuthProvider, 
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut 
} from 'firebase/auth'
import { doc, setDoc, getDoc, serverTimestamp } from 'firebase/firestore'
import { ref, uploadBytes, getDownloadURL } from 'firebase/storage'

export default function AuthPanel({ isOpen, onClose, isAuthenticated, onLogin, onLogout, initialView = 'menu' }) {
  const [view, setView] = useState('menu') 
  const [tab, setTab] = useState('login')
  const [onboardingStep, setOnboardingStep] = useState(1)
  
  // NEW: Registration Success State
  const [registrationSuccess, setRegistrationSuccess] = useState(false)
  
  // Auth State
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  // Profile State
  const [profile, setProfile] = useState({
    fullName: '',
    age: '',
    phone: '',
    qualification: '',
    isDoctor: 'yes',
    affiliation: '',
    certificateUrl: '',
    isVerified: false
  })
  const [file, setFile] = useState(null)
  const [userData, setUserData] = useState(null)

  useEffect(() => {
    if (isOpen) {
      setView(initialView)
      setRegistrationSuccess(false) // Reset on open
      if (isAuthenticated) fetchUserData()
    }
  }, [isOpen, initialView, isAuthenticated])

  const fetchUserData = async () => {
    if (!auth.currentUser) return
    const docRef = doc(db, 'users', auth.currentUser.uid)
    const docSnap = await getDoc(docRef)
    if (docSnap.exists()) setUserData(docSnap.data())
  }

  const handleGoogleLogin = async () => {
    try {
      setLoading(true)
      const provider = new GoogleAuthProvider()
      const result = await signInWithPopup(auth, provider)
      
      const userRef = doc(db, 'users', result.user.uid)
      const userSnap = await getDoc(userRef)
      
      if (!userSnap.exists()) {
        setProfile(prev => ({ ...prev, fullName: result.user.displayName || '' }))
        setView('auth')
        setTab('register')
        setOnboardingStep(2)
      } else {
        onLogin()
        onClose()
      }
    } catch (err) { setError(err.message) }
    finally { setLoading(false) }
  }

  const handleAuthAction = async () => {
    setError(null)
    setLoading(true)
    try {
      if (tab === 'login') {
        await signInWithEmailAndPassword(auth, email, password)
        onLogin()
        onClose()
      } else {
        if (password.length < 6) throw new Error("Password must be at least 6 characters")
        setOnboardingStep(2)
      }
    } catch (err) { setError(err.message) }
    finally { setLoading(false) }
  }

  const handleProfileSubmit = async () => {
    setError(null)
    if (!profile.fullName || !profile.age || !profile.phone || !profile.qualification) {
      setError("Please fill all mandatory (*) fields")
      return
    }

    setLoading(true)
    try {
      let finalCertUrl = ''
      
      let userUid = auth.currentUser?.uid
      if (!userUid) {
        const cred = await createUserWithEmailAndPassword(auth, email, password)
        userUid = cred.user.uid
      }

      if (file) {
        const storageRef = ref(storage, `verifications/${userUid}_${Date.now()}_${file.name}`)
        const snapshot = await uploadBytes(storageRef, file)
        finalCertUrl = await getDownloadURL(snapshot.ref)
      } else {
        throw new Error("Medical certificate upload is mandatory for verification.")
      }

      const userPayload = {
        uid: userUid,
        email: auth.currentUser?.email || email,
        ...profile,
        certificateUrl: finalCertUrl,
        createdAt: serverTimestamp(),
        isVerified: false,
        caseCount: 0
      }
      
      await setDoc(doc(db, 'users', userUid), userPayload)
      onLogin()
      
      // NEW: Show Success Screen instead of closing
      setRegistrationSuccess(true)
      
    } catch (err) { setError(err.message) }
    finally { setLoading(false) }
  }

  const handleLogout = async () => {
    await signOut(auth)
    onLogout()
    setView('menu')
    setUserData(null)
  }

  const renderContent = () => {
    // NEW: Success Screen View
    if (registrationSuccess) {
      return (
        <div style={styles.successView}>
          <div style={styles.successIconWrapper}>
            <CheckCircle2 size={64} color="#10B981" />
          </div>
          <h2 style={styles.successTitle}>Registration Submitted</h2>
          <p style={styles.successMessage}>
            Thank you for joining HawkFranklin Research. Your profile and certificate have been securely uploaded. Our team will verify your credentials shortly.
          </p>
          <button 
            className="btn-primary" 
            onClick={onClose} 
            style={{ width: '100%', padding: '16px', borderRadius: '16px', marginTop: '20px' }}
          >
            Continue to Dashboard
          </button>
        </div>
      )
    }

    if (view === 'about') {
      return (
        <div style={styles.contentWrap}>
          <button style={styles.backLink} onClick={() => setView('menu')}><ArrowLeft size={16} /> Back</button>
          <h2 style={styles.sectionTitle}>About HawkFranklin</h2>
          <p style={styles.text}>HawkFranklin Research is a <strong>Non-Profit Research Organization</strong> evolving into a global product innovation company that combines deep science with engineering.</p>
          <p style={styles.text}>We operate with a dual mission: our researchers explore fundamental scientific problems while our engineers convert those findings into demonstrable clinical prototypes.</p>
          <p style={styles.text}>Our focus is Clinical AI, including cancer genomic risk prediction, pathology-based survival analysis, and intelligent dermatology tools.</p>
          <a href="https://www.hawkfranklin.in" target="_blank" style={styles.webLink}>www.hawkfranklin.in <ExternalLink size={14}/></a>
        </div>
      )
    }

    if (view === 'validation') {
      return (
        <div style={styles.contentWrap}>
          <button style={styles.backLink} onClick={() => setView('menu')}><ArrowLeft size={16} /> Back</button>
          <h2 style={styles.sectionTitle}>Clinical Validation</h2>
          <p style={styles.text}>By participating, you are validating AI models developed from public datasets. We see this as a generational journey—contribution today ensures that future generations will rely on clinically-proven AI.</p>
          <div style={styles.incentiveCard}>
            <Award size={20} color="var(--accent-gold)" />
            <div>
              <h4 style={{fontSize:'14px', marginBottom:'4px'}}>Publication Credit</h4>
              <p style={{fontSize:'12px', color:'var(--text-muted)'}}>Complete 100 cases to be directly incorporated into a High-Impact research paper published by our team.</p>
            </div>
          </div>
          <div style={styles.incentiveCard}>
            <BookOpen size={20} color="var(--accent-cyan)" />
            <div>
              <h4 style={{fontSize:'14px', marginBottom:'4px'}}>2-Month Fellowship</h4>
              <p style={{fontSize:'12px', color:'var(--text-muted)'}}>Receive an official certificate of your time in clinical validation studies upon completion.</p>
            </div>
          </div>
          <p style={styles.text}><strong>Data Privacy:</strong> Your data will <u>never</u> be sold to private partners. It is used strictly for non-profit clinical research.</p>
        </div>
      )
    }

    if (view === 'auth') {
      if (tab === 'login') {
        return (
          <div style={styles.contentWrap}>
            <button style={styles.backLink} onClick={() => setView('menu')}><ArrowLeft size={16} /> Back</button>
            <h2 style={styles.sectionTitle}>Welcome Back</h2>
            {error && <p style={styles.error}>{error}</p>}
            <input type="email" style={styles.input} placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
            <input type="password" style={styles.input} placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} />
            <button className="btn-primary" onClick={handleAuthAction} disabled={loading} style={{ width: '100%', marginTop:'10px' }}>
              {loading ? 'Authenticating...' : 'Sign In'}
            </button>
            <div style={styles.divider}>OR</div>
            <button className="btn-secondary" style={{ width: '100%' }} onClick={handleGoogleLogin}>Continue with Google</button>
            <p style={{textAlign:'center', marginTop:'20px', fontSize:'13px'}}>New researcher? <span style={{color:'var(--accent-cyan)', cursor:'pointer', fontWeight:700}} onClick={() => setTab('register')}>Register here</span></p>
          </div>
        )
      }

      // REGISTER / ONBOARDING FLOW
      return (
        <div style={styles.contentWrap}>
          <button style={styles.backLink} onClick={() => setView('menu')}><ArrowLeft size={16} /> Back</button>
          <div style={styles.stepIndicator}>Step {onboardingStep} of 2</div>
          <h2 style={styles.sectionTitle}>{onboardingStep === 1 ? 'Create Account' : 'Clinician Profile'}</h2>
          
          {error && <p style={styles.error}>{error}</p>}

          {onboardingStep === 1 ? (
            <>
              <input type="email" style={styles.input} placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
              <input type="password" style={styles.input} placeholder="Password (min 6 chars)" value={password} onChange={(e) => setPassword(e.target.value)} />
              <button className="btn-primary" onClick={handleAuthAction} style={{ width: '100%', marginTop:'10px' }}>Continue to Profile</button>
              <div style={styles.divider}>OR</div>
              <button className="btn-secondary" style={{ width: '100%' }} onClick={handleGoogleLogin}>Register with Google</button>
            </>
          ) : (
            <div style={{maxHeight: '60vh', overflowY: 'auto', paddingRight: '4px'}}>
              <label style={styles.fieldLabel}>Full Name*</label>
              <input style={styles.input} value={profile.fullName} onChange={e => setProfile({...profile, fullName: e.target.value})} placeholder="Dr. Jane Doe"/>
              
              <div style={{display:'flex', gap:'10px'}}>
                <div style={{flex:1}}>
                  <label style={styles.fieldLabel}>Age*</label>
                  <input type="number" style={styles.input} value={profile.age} onChange={e => setProfile({...profile, age: e.target.value})}/>
                </div>
                <div style={{flex:2}}>
                  <label style={styles.fieldLabel}>Phone Number*</label>
                  <input style={styles.input} value={profile.phone} onChange={e => setProfile({...profile, phone: e.target.value})}/>
                </div>
              </div>

              <label style={styles.fieldLabel}>Are you a Doctor?*</label>
              <select style={styles.input} value={profile.isDoctor} onChange={e => setProfile({...profile, isDoctor: e.target.value})}>
                <option value="yes">Yes, I am a clinician</option>
                <option value="student">No, I am a medical student</option>
                <option value="other">Other researcher</option>
              </select>

              <label style={styles.fieldLabel}>Educational Qualification*</label>
              <input style={styles.input} value={profile.qualification} onChange={e => setProfile({...profile, qualification: e.target.value})} placeholder="MBBS, MD, etc."/>

              <label style={styles.fieldLabel}>University/Institution Affiliation (Optional)</label>
              <p style={{fontSize:'11px', color:'var(--text-muted)', marginBottom:'8px'}}>Used for publication credits. Leave blank to be affiliated under HawkFranklin.</p>
              <input style={styles.input} value={profile.affiliation} onChange={e => setProfile({...profile, affiliation: e.target.value})}/>

              <label style={styles.fieldLabel}>Upload Degree/Certificate*</label>
              <div style={styles.uploadBox} onClick={() => document.getElementById('cert-up').click()}>
                {file ? <div style={{display:'flex', alignItems:'center', gap:8}}><CheckCircle2 color="#10B981" size={20}/> {file.name}</div> : <><Upload size={20}/> Click to upload PDF/Image</>}
                <input id="cert-up" type="file" hidden onChange={e => setFile(e.target.files[0])}/>
              </div>

              <p style={{fontSize:'11px', color:'var(--text-muted)', marginTop:'20px', lineHeight:1.4}}>
                * Note: Upon completion of 100 clinical evaluations, you will receive an official certificate confirming 2 months of clinical validation service.
              </p>

              <button className="btn-primary" onClick={handleProfileSubmit} disabled={loading} style={{ width: '100%', marginTop:'20px', padding:'18px' }}>
                {loading ? 'Submitting...' : 'Complete Registration'}
              </button>
            </div>
          )}
        </div>
      )
    }

    if (isAuthenticated) {
      return (
        <div style={styles.profileView}>
          <div style={styles.profileCard}>
            <div style={styles.avatarLarge}>
              {auth.currentUser?.email?.[0].toUpperCase()}
              {userData?.isVerified && <div style={styles.verifiedBadge}><BadgeCheck size={20} color="#FFF" fill="#10B981"/></div>}
            </div>
            <h3 style={{fontSize:'20px', marginTop:'12px'}}>{userData?.fullName || auth.currentUser?.displayName || 'Clinician'}</h3>
            <p style={{fontSize:'13px', color:'var(--text-muted)'}}>{auth.currentUser?.email}</p>
            
            {userData?.isVerified ? (
               <div style={styles.statusTagVerified}>✓ Verified Clinician</div>
            ) : (
               <div style={styles.statusTagPending}>Verification Pending</div>
            )}
          </div>

          <div style={styles.statGrid}>
            <div style={styles.statItem}>
              <span style={styles.statVal}>{userData?.caseCount || 0}</span>
              <span style={styles.statLabel}>Evaluations</span>
            </div>
            <div style={styles.statItem}>
              <span style={styles.statVal}>{Math.min(100, userData?.caseCount || 0)}%</span>
              <span style={styles.statLabel}>To Certificate</span>
            </div>
          </div>

          <div style={{marginTop:'auto', width:'100%'}}>
            <button style={{...styles.menuItem, width:'100%', marginBottom:'10px'}} onClick={() => setView('about')}>About us</button>
            <button style={{...styles.menuItem, width:'100%', color:'#EF4444'}} onClick={handleLogout}>Sign Out</button>
          </div>
        </div>
      )
    }

    return (
      <div style={styles.menuList}>
        <button style={styles.menuItem} onClick={() => setView('about')}>
          <div style={styles.menuIcon}><Globe size={18}/></div>
          <span>About HawkFranklin</span>
          <ChevronRight size={16} color="var(--text-muted)"/>
        </button>

        <button style={styles.menuItem} onClick={() => setView('validation')}>
          <div style={styles.menuIcon}><Award size={18}/></div>
          <span>Why Validate with us?</span>
          <ChevronRight size={16} color="var(--text-muted)"/>
        </button>

        <button style={{...styles.menuItem, opacity: 0.4, cursor: 'not-allowed'}}>
          <div style={styles.menuIcon}><BookOpen size={18}/></div>
          <span>Publications</span>
          <span style={{fontSize:'10px', marginLeft:'auto', background:'#EEE', padding:'2px 6px', borderRadius:'4px'}}>Soon</span>
        </button>

        <button style={{...styles.menuItem, marginTop: '20px', background: 'var(--surface-cyan-light)'}} onClick={() => setView('auth')}>
          <div style={styles.menuIcon}><User size={18}/></div>
          <span>Sign In / Register</span>
          <ChevronRight size={16}/>
        </button>
      </div>
    )
  }

  return (
    <>
      <div style={{...styles.overlay, opacity: isOpen ? 1 : 0, pointerEvents: isOpen ? 'auto' : 'none'}} onClick={onClose} />
      <aside style={{...styles.panel, right: isOpen ? 0 : '-100%'}}>
        <div style={styles.header}>
          <button style={styles.closeBtn} onClick={onClose}><X size={24}/></button>
        </div>
        {renderContent()}
        <footer style={styles.footer}>
          <p>© 2026 HawkFranklin Research</p>
          <p>Non-Profit Organization</p>
        </footer>
      </aside>
    </>
  )
}

const ArrowLeft = ({size}) => <ChevronRight size={size} style={{transform:'rotate(180deg)'}}/>

const styles = {
  overlay: { position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', background: 'rgba(0,0,0,0.2)', zIndex: 199, transition: 'opacity 0.4s' },
  panel: {
    position: 'absolute', top: 0, width: '85%', maxWidth: '400px', height: '100%',
    background: '#FFF', zIndex: 200, display: 'flex', flexDirection: 'column', 
    padding: '40px 24px 24px', transition: 'right 0.4s cubic-bezier(0.16, 1, 0.3, 1)', 
    boxShadow: '-10px 0 30px rgba(0,0,0,0.05)'
  },
  header: { display: 'flex', justifyContent: 'flex-end', marginBottom: '20px' },
  closeBtn: { background: 'none', border: 'none', cursor: 'pointer' },
  menuList: { display: 'flex', flexDirection: 'column', gap: '8px' },
  menuItem: {
    display: 'flex', alignItems: 'center', padding: '16px', borderRadius: '14px',
    background: '#F8FAFC', border: '1px solid rgba(0,0,0,0.03)', cursor: 'pointer',
    textAlign: 'left', fontSize: '15px', fontWeight: 500, gap: '12px'
  },
  menuIcon: { width: '32px', height: '32px', borderRadius: '8px', background: '#FFF', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 2px 4px rgba(0,0,0,0.04)' },
  contentWrap: { display: 'flex', flexDirection: 'column' },
  backLink: { background: 'none', border: 'none', color: 'var(--accent-cyan)', fontSize: '14px', fontWeight: 600, cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px', marginBottom: '20px', alignSelf: 'flex-start' },
  sectionTitle: { fontSize: '22px', fontWeight: 800, marginBottom: '16px' },
  text: { fontSize: '14px', color: '#475569', lineHeight: 1.6, marginBottom: '16px' },
  webLink: { color: 'var(--accent-gold-dark)', textDecoration: 'none', fontWeight: 700, fontSize: '14px', display: 'flex', alignItems: 'center', gap: '6px' },
  incentiveCard: { display: 'flex', gap: '12px', background: '#F8FAFC', padding: '12px', borderRadius: '12px', marginBottom: '10px', border: '1px solid rgba(0,0,0,0.02)' },
  stepIndicator: { fontSize: '11px', fontWeight: 700, color: 'var(--accent-gold)', textTransform: 'uppercase', marginBottom: '8px' },
  fieldLabel: { display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '6px', color: '#64748B' },
  input: { width: '100%', padding: '14px', borderRadius: '12px', border: '1px solid #E2E8F0', marginBottom: '16px', outline: 'none', fontSize: '14px' },
  uploadBox: { border: '2px dashed #E2E8F0', borderRadius: '14px', padding: '20px', textAlign: 'center', cursor: 'pointer', color: '#64748B', fontSize: '13px', display:'flex', flexDirection:'column', alignItems:'center', gap:'10px' },
  divider: { textAlign: 'center', margin: '16px 0', fontSize: '12px', color: '#94A3B8' },
  error: { color: '#EF4444', fontSize: '12px', marginBottom: '12px', background: '#FEE2E2', padding: '8px', borderRadius: '6px' },
  tabs: { display: 'flex', gap: '24px', marginBottom: '20px' },
  tab: { paddingBottom: '8px', cursor: 'pointer', fontWeight: 700, borderBottom: '2px solid' },
  profileView: { display: 'flex', flexDirection: 'column', height: '100%' },
  profileCard: { textAlign: 'center', padding: '24px', background: '#F8FAFC', borderRadius: '24px', marginBottom: '20px' },
  avatarLarge: { position: 'relative', width: '70px', height: '70px', borderRadius: '50%', background: 'var(--accent-gold)', color: '#FFF', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '24px', fontWeight: 800, margin: '0 auto' },
  verifiedBadge: { position: 'absolute', bottom: -2, right: -2, background: '#10B981', borderRadius: '50%', border: '2px solid #FFF' },
  statusTagVerified: { display: 'inline-block', marginTop: '16px', padding: '4px 12px', borderRadius: '20px', background: '#D1FAE5', color: '#065F46', fontSize: '11px', fontWeight: 700 },
  statusTagPending: { display: 'inline-block', marginTop: '16px', padding: '4px 12px', borderRadius: '20px', background: '#FEF3C7', color: '#92400E', fontSize: '11px', fontWeight: 700 },
  statGrid: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '20px' },
  statItem: { padding: '16px', background: '#F8FAFC', borderRadius: '16px', textAlign: 'center' },
  statVal: { display: 'block', fontSize: '18px', fontWeight: 800, color: 'var(--accent-cyan)' },
  statLabel: { fontSize: '11px', color: '#64748B', fontWeight: 600 },
  footer: { marginTop: 'auto', textAlign: 'center', fontSize: '11px', color: '#94A3B8', borderTop: '1px solid #F1F5F9', paddingTop: '20px' },
  successView: { display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%', textAlign: 'center', padding: '20px' },
  successIconWrapper: { width: '80px', height: '80px', borderRadius: '50%', background: '#D1FAE5', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '24px' },
  successTitle: { fontSize: '24px', fontWeight: 800, color: '#1E293B', marginBottom: '16px' },
  successMessage: { fontSize: '15px', color: '#475569', lineHeight: 1.6, marginBottom: '32px' }
}