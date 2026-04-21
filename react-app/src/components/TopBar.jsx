import React from 'react'
import { Menu } from 'lucide-react'
import hawkFranklinLogo from '../assets/hawkfranklin-logo.png'

export default function TopBar({ onProfileClick, isAuthenticated }) {
  return (
    <header style={styles.header}>
      <button
        type="button"
        aria-label={isAuthenticated ? 'Open profile menu' : 'Open sign in menu'}
        style={styles.profileBtn}
        onClick={onProfileClick}
      >
        <Menu color="var(--text-main)" size={22} />
      </button>

      <div style={styles.brand} aria-label="HawkFranklin Research">
        <div style={styles.brandLogoFrame}>
          <img src={hawkFranklinLogo} alt="HawkFranklin" style={styles.brandLogo} />
        </div>
        <div style={styles.brandText}>
          <h1 style={styles.title}>HawkFranklin</h1>
          <p style={styles.subtitle}>RESEARCH</p>
        </div>
      </div>
    </header>
  )
}

const styles = {
  header: {
    position: 'relative',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '36px 24px 22px',
    textAlign: 'center',
    zIndex: 10
  },
  brand: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: '18px',
    width: '100%'
  },
  brandText: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center'
  },
  brandLogoFrame: {
    width: '136px',
    height: '136px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    flexShrink: 0,
    borderRadius: '34px',
    background: 'linear-gradient(180deg, rgba(255, 247, 230, 0.95) 0%, rgba(255, 255, 255, 0.78) 100%)',
    border: '1px solid rgba(197, 160, 40, 0.12)',
    boxShadow: '0 22px 44px rgba(197, 160, 40, 0.16)'
  },
  brandLogo: {
    width: '76%',
    height: '76%',
    objectFit: 'contain',
    filter: 'drop-shadow(0 18px 28px rgba(197, 160, 40, 0.22))'
  },
  title: {
    fontSize: 'clamp(34px, 7vw, 46px)',
    fontWeight: 800,
    letterSpacing: '-1.4px',
    lineHeight: 1
  },
  subtitle: {
    fontSize: '13px',
    fontWeight: 700,
    color: 'var(--accent-gold-dark)',
    fontFamily: 'Outfit',
    textTransform: 'uppercase',
    letterSpacing: '7px',
    marginTop: '8px',
    width: '100%',
    textAlign: 'center'
  },
  profileBtn: {
    position: 'absolute',
    top: '24px',
    right: '24px',
    width: '52px',
    height: '52px',
    borderRadius: '16px',
    background: 'rgba(255, 255, 255, 0.95)',
    border: '1px solid rgba(20, 20, 22, 0.06)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    cursor: 'pointer',
    boxShadow: '0 12px 24px rgba(20, 20, 22, 0.08)'
  }
}
