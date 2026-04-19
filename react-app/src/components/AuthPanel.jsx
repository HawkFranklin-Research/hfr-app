import React, { useState } from 'react'
import { X, Globe, Shield, Award, BookOpen, ChevronRight, User, ExternalLink } from 'lucide-react'
import { auth } from '../config/firebase'
import { 
  signInWithPopup, 
  GoogleAuthProvider, 
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut 
} from 'firebase/auth'

export default function AuthPanel({ isOpen, onClose, isAuthenticated, onLogin, onLogout }) {
  const [view, setView] = useState('menu') // 'menu', 'about', 'validation', 'auth'
  const [tab, setTab] = useState('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)

  const handleGoogleLogin = async () => {
    try {
      const provider = new GoogleAuthProvider()
      await signInWithPopup(auth, provider)
      onLogin()
      onClose()
    } catch (err) { setError(err.message) }
  }

  const handleEmailAuth = async () => {
    try {
      setError(null)
      if (tab === 'login') await signInWithEmailAndPassword(auth, email, password)
      else await createUserWithEmailAndPassword(auth, email, password)
      onLogin()
      onClose()
    } catch (err) { setError(err.message) }
  }

  const handleLogout = async () => {
    try {
      await signOut(auth)
      onLogout()
      setView('menu')
    } catch (err) { setError(err.message) }
  }

  const renderContent = () => {
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
              <p style={{fontSize:'12px', color:'var(--text-muted)'}}>Complete your case quota to be directly incorporated into a High-Impact research paper published by our team.</p>
            </div>
          </div>
          <div style={styles.incentiveCard}>
            <BookOpen size={20} color="var(--accent-cyan)" />
            <div>
              <h4 style={{fontSize:'14px', marginBottom:'4px'}}>Pre-print Access</h4>
              <p style={{fontSize:'12px', color:'var(--text-muted)'}}>Get early access to our latest research findings and technical write-ups.</p>
            </div>
          </div>
          <p style={styles.text}><strong>Data Privacy:</strong> Your data will <u>never</u> be sold to private partners. It is used strictly for non-profit clinical research.</p>
        </div>
      )
    }

    if (view === 'auth') {
      return (
        <div style={styles.contentWrap}>
          <button style={styles.backLink} onClick={() => setView('menu')}><ArrowLeft size={16} /> Back</button>
          <div style={styles.tabs}>
            <div style={{...styles.tab, color: tab === 'login' ? 'var(--accent-cyan)' : 'var(--text-muted)', borderBottomColor: tab === 'login' ? 'var(--accent-cyan)' : 'transparent'}} onClick={() => setTab('login')}>Sign In</div>
            <div style={{...styles.tab, color: tab === 'register' ? 'var(--accent-cyan)' : 'var(--text-muted)', borderBottomColor: tab === 'register' ? 'var(--accent-cyan)' : 'transparent'}} onClick={() => setTab('register')}>Register</div>
          </div>
          {error && <p style={styles.error}>{error}</p>}
          <input type="email" style={styles.input} placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
          <input type="password" style={styles.input} placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} />
          <button className="btn-primary" onClick={handleEmailAuth} style={{ width: '100%', marginTop:'10px' }}>{tab === 'login' ? 'Sign In' : 'Register'}</button>
          <div style={styles.divider}>OR</div>
          <button className="btn-secondary" style={{ width: '100%' }} onClick={handleGoogleLogin}>Continue with Google</button>
        </div>
      )
    }

    // Default Menu
    return (
      <div style={styles.menuList}>
        {isAuthenticated && (
          <div style={styles.profileSummary}>
            <div style={styles.avatar}>{auth.currentUser?.email?.[0].toUpperCase()}</div>
            <div>
              <h3 style={{fontSize:'16px'}}>{auth.currentUser?.displayName || 'Clinician'}</h3>
              <p style={{fontSize:'12px', color:'var(--text-muted)'}}>{auth.currentUser?.email}</p>
            </div>
          </div>
        )}
        
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

        {!isAuthenticated ? (
          <button style={{...styles.menuItem, marginTop: '20px', background: 'var(--surface-cyan-light)'}} onClick={() => setView('auth')}>
            <div style={styles.menuIcon}><User size={18}/></div>
            <span>Sign In / Register</span>
            <ChevronRight size={16}/>
          </button>
        ) : (
          <button style={{...styles.menuItem, marginTop: '20px'}} onClick={handleLogout}>
            <span>Sign Out</span>
          </button>
        )}
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
    position: 'absolute', top: 0, width: '85%', maxWidth: '380px', height: '100%',
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
  tabs: { display: 'flex', gap: '20px', marginBottom: '24px' },
  tab: { paddingBottom: '8px', cursor: 'pointer', fontWeight: 700, borderBottom: '2px solid' },
  input: { width: '100%', padding: '14px', borderRadius: '12px', border: '1px solid #E2E8F0', marginBottom: '12px', outline: 'none' },
  divider: { textAlign: 'center', margin: '16px 0', fontSize: '12px', color: '#94A3B8' },
  error: { color: '#EF4444', fontSize: '12px', marginBottom: '12px', background: '#FEE2E2', padding: '8px', borderRadius: '6px' },
  profileSummary: { display: 'flex', alignItems: 'center', gap: '12px', padding: '16px', background: 'var(--surface-cyan-light)', borderRadius: '14px', marginBottom: '12px' },
  avatar: { width: '40px', height: '40px', borderRadius: '50%', background: 'var(--accent-cyan)', color: '#FFF', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700 },
  footer: { marginTop: 'auto', textAlign: 'center', fontSize: '11px', color: '#94A3B8', borderTop: '1px solid #F1F5F9', paddingTop: '20px' }
}
