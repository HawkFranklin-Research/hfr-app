import React from 'react'
import { User, UserCheck } from 'lucide-react'
import hawkFranklinLogo from '../assets/hawkfranklin-logo.png'

export default function TopBar({ onProfileClick, isAuthenticated }) {
  return (
    <header style={styles.header}>
      <div style={styles.brand}>
        <div style={styles.brandLogoFrame}>
          <img src={hawkFranklinLogo} alt="HawkFranklin" style={styles.brandLogo} />
        </div>
        <div style={styles.brandText}>
          <h1 style={styles.title}>HawkFranklin</h1>
          <p style={styles.subtitle}>Research</p>
        </div>
      </div>
      <button style={styles.profileBtn} onClick={onProfileClick}>
        {isAuthenticated ? <UserCheck color="var(--accent-cyan)" /> : <User color="var(--text-main)" />}
      </button>
    </header>
  )
}

const styles = {
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '20px 24px',
    zIndex: 10
  },
  brand: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px'
  },
  brandText: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center'
  },
  brandLogoFrame: {
    width: '48px',
    height: '48px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    flexShrink: 0
  },
  brandLogo: {
    width: '48px',
    height: '48px',
    objectFit: 'contain',
    filter: 'drop-shadow(0 10px 18px rgba(197, 160, 40, 0.18))'
  },
  title: {
    fontSize: '20px',
    letterSpacing: '0.5px'
  },
  subtitle: {
    fontSize: '12px',
    color: 'var(--accent-gold-dark)',
    fontFamily: 'Inter',
    textTransform: 'uppercase',
    letterSpacing: '1px'
  },
  profileBtn: {
    width: '44px', height: '44px',
    borderRadius: '50%',
    background: 'rgba(255, 255, 255, 0.9)',
    border: '1px solid var(--glass-border)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    cursor: 'pointer',
    boxShadow: 'var(--shadow-card)'
  }
}
