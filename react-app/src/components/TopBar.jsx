import React from 'react'
import { User, UserCheck, Menu } from 'lucide-react'
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
          <p style={styles.subtitle}>RESEARCH</p>
        </div>
      </div>
      <button style={styles.profileBtn} onClick={onProfileClick}>
        <Menu color="var(--text-main)" size={24} />
      </button>
    </header>
  )
}

const styles = {
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '30px 24px',
    zIndex: 10
  },
  brand: {
    display: 'flex',
    alignItems: 'center',
    gap: '16px'
  },
  brandText: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center' // Centered Research under Title
  },
  brandLogoFrame: {
    width: '64px',
    height: '64px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    flexShrink: 0
  },
  brandLogo: {
    width: '100%',
    height: '100%',
    objectFit: 'contain',
    filter: 'drop-shadow(0 12px 24px rgba(197, 160, 40, 0.25))'
  },
  title: {
    fontSize: '32px',
    fontWeight: 800,
    letterSpacing: '-0.5px',
    lineHeight: 1
  },
  subtitle: {
    fontSize: '14px',
    fontWeight: 700,
    color: 'var(--accent-gold-dark)',
    fontFamily: 'Outfit',
    textTransform: 'uppercase',
    letterSpacing: '5px', // More spacing for centered look
    marginTop: '4px',
    width: '100%',
    textAlign: 'center'
  },
  profileBtn: {
    width: '52px',
    height: '52px',
    borderRadius: '16px',
    background: 'rgba(255, 255, 255, 0.95)',
    border: '1px solid var(--glass-border)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    cursor: 'pointer',
    boxShadow: '0 8px 16px rgba(20, 20, 22, 0.06)'
  }
}
