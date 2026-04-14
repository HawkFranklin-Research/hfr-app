import React, { useState } from 'react'
import { X } from 'lucide-react'
import { auth } from '../config/firebase'
import { 
  signInWithPopup, 
  GoogleAuthProvider, 
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut 
} from 'firebase/auth'

export default function AuthPanel({ isOpen, onClose, isAuthenticated, onLogin, onLogout }) {
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
    } catch (err) {
      setError(err.message)
    }
  }

  const handleEmailAuth = async () => {
    try {
      setError(null)
      if (tab === 'login') {
        await signInWithEmailAndPassword(auth, email, password)
      } else {
        await createUserWithEmailAndPassword(auth, email, password)
      }
      onLogin()
      onClose()
    } catch (err) {
      setError(err.message)
    }
  }

  const handleLogout = async () => {
    try {
      await signOut(auth)
      onLogout()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <>
      <div 
        style={{...styles.overlay, opacity: isOpen ? 1 : 0, pointerEvents: isOpen ? 'auto' : 'none'}} 
        onClick={onClose} 
      />
      
      <aside style={{...styles.panel, right: isOpen ? 0 : '-100%'}}>
        <div style={styles.header}>
          <button style={styles.closeBtn} onClick={onClose}><X color="var(--text-main)" size={24}/></button>
          <h2 style={{ fontSize: '24px', marginLeft: '12px' }}>Account</h2>
        </div>

        {!isAuthenticated ? (
          <div>
            <div style={styles.tabs}>
              <div 
                style={{...styles.tab, color: tab === 'login' ? 'var(--accent-cyan)' : 'var(--text-muted)', borderBottomColor: tab === 'login' ? 'var(--accent-cyan)' : 'var(--glass-border)'}}
                onClick={() => setTab('login')}
              >Sign In</div>
              <div 
                style={{...styles.tab, color: tab === 'register' ? 'var(--accent-cyan)' : 'var(--text-muted)', borderBottomColor: tab === 'register' ? 'var(--accent-cyan)' : 'var(--glass-border)'}}
                onClick={() => setTab('register')}
              >Register</div>
            </div>

            {error && <p style={{ color: '#ef4444', fontSize: '13px', marginBottom: '16px', background: '#fee2e2', padding: '10px', borderRadius: '8px' }}>{error}</p>}

            <div style={{ marginBottom: '20px' }}>
              <label style={styles.label}>Email Address</label>
              <input 
                type="email" 
                style={styles.input} 
                placeholder="clinician@example.com" 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div style={{ marginBottom: '20px' }}>
              <label style={styles.label}>Password</label>
              <input 
                type="password" 
                style={styles.input} 
                placeholder="••••••••" 
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            <button className="btn-primary" onClick={handleEmailAuth} style={{ width: '100%' }}>
              {tab === 'login' ? 'Sign In' : 'Register'}
            </button>

            <div style={styles.divider}>OR CONTINUE WITH</div>

            <button className="btn-secondary" style={{ ...styles.btnGoogle, width: '100%' }} onClick={handleGoogleLogin}>
              Google
            </button>
          </div>
        ) : (
          <div style={styles.profileView}>
            <div style={styles.avatar}>
              {auth.currentUser?.displayName?.[0] || auth.currentUser?.email?.[0] || 'DR'}
            </div>
            <h3 style={{ fontSize: '20px', marginBottom: '5px' }}>
              {auth.currentUser?.displayName || 'Dr. Clinician'}
            </h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '14px', marginBottom: '30px' }}>
              {auth.currentUser?.email}
            </p>
            
            <div style={{ width: '100%', marginBottom: '20px' }}>
              <div style={styles.statRow}>
                <span style={{ color: 'var(--text-muted)' }}>Cases Reviewed</span>
                <span style={{ fontWeight: 700, color: 'var(--accent-cyan)' }}>142</span>
              </div>
              <div style={styles.statRow}>
                <span style={{ color: 'var(--text-muted)' }}>Current Tier</span>
                <span style={{ fontWeight: 700, color: 'var(--accent-gold)' }}>Senior</span>
              </div>
            </div>

            <button className="btn-secondary" style={{ marginTop: 'auto', width: '100%' }} onClick={handleLogout}>Sign Out</button>
          </div>
        )}
      </aside>
    </>
  )
}

const styles = {
  overlay: {
    position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
    background: 'rgba(20, 20, 22, 0.16)', zIndex: 199, transition: 'opacity 0.4s'
  },
  panel: {
    position: 'absolute', top: 0, width: '85%', maxWidth: '400px', height: '100%',
    background: 'rgba(255, 255, 255, 0.94)', backdropFilter: 'blur(20px)',
    borderLeft: '1px solid var(--glass-border)',
    zIndex: 200, display: 'flex', flexDirection: 'column', 
    padding: 'env(safe-area-inset-top, 40px) 24px 24px',
    transition: 'right 0.4s cubic-bezier(0.16, 1, 0.3, 1)', 
    boxShadow: '-16px 0 32px rgba(20, 20, 22, 0.08)'
  },
  header: { display: 'flex', alignItems: 'center', marginBottom: '40px' },
  closeBtn: { background: 'none', border: 'none', cursor: 'pointer', padding: '8px', marginLeft: '-8px' },
  tabs: { display: 'flex', gap: '12px', marginBottom: '32px' },
  tab: { flex: 1, padding: '12px 0', textAlign: 'center', borderBottom: '2px solid', fontWeight: 600, cursor: 'pointer' },
  label: { display: 'block', fontSize: '12px', color: 'var(--text-muted)', marginBottom: '8px', textTransform: 'uppercase' },
  input: {
    width: '100%', padding: '16px', borderRadius: '12px',
    background: '#FFFFFF', border: '1px solid var(--glass-border)',
    color: 'var(--text-main)', fontSize: '16px', outline: 'none',
    boxSizing: 'border-box'
  },
  divider: { textAlign: 'center', margin: '30px 0', color: 'var(--text-muted)', fontSize: '12px' },
  btnGoogle: { background: '#FFFFFF', color: 'var(--text-main)' },
  profileView: { display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: '40px' },
  avatar: {
    width: '80px', height: '80px', borderRadius: '50%',
    background: 'linear-gradient(135deg, var(--accent-cyan), var(--accent-gold))',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    fontSize: '32px', fontWeight: 700, marginBottom: '16px', color: '#000',
    boxShadow: '0 12px 24px rgba(34, 211, 238, 0.14)'
  },
  statRow: { display: 'flex', justifyContent: 'space-between', padding: '12px 0', borderBottom: '1px solid var(--glass-border)', width: '100%' }
}
