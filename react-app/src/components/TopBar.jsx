import React from 'react'
import { Menu } from 'lucide-react'
import hawkFranklinLogo from '../assets/hawkfranklin-logo.png'

export default function TopBar({ onProfileClick, isAuthenticated }) {
  return (
    <header style={styles.header}>
      <div style={styles.brandBlock}>
        <div style={styles.mainRow}>
          <div style={styles.brandLogoFrame}>
            <img src={hawkFranklinLogo} alt="HawkFranklin" style={styles.brandLogo} />
          </div>
          <h1 style={styles.title}>HawkFranklin</h1>
        </div>
        <div style={styles.subRow}>
          <p style={styles.subtitle}>RESEARCH</p>
        </div>
      </div>

      <button
        type="button"
        aria-label={isAuthenticated ? 'Open profile menu' : 'Open sign in menu'}
        style={styles.profileBtn}
        onClick={onProfileClick}
      >
        <Menu color="var(--text-main)" size={22} />
      </button>
    </header>
  )
}

const styles = {
  header: {
    position: 'relative',
    display: 'flex',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    padding: '36px 24px 22px',
    zIndex: 10
  },
  brandBlock: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center', // This centers the subRow content (RESEARCH) relative to the mainRow
  },
  mainRow: {
    display: 'flex',
    alignItems: 'center',
    gap: '16px'
  },
  subRow: {
    width: '100%',
    display: 'flex',
    justifyContent: 'center',
    marginTop: '6px'
  },
  brandLogoFrame: {
    width: '84px',
    height: '84px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    flexShrink: 0,
    borderRadius: '20px',
    background: 'linear-gradient(180deg, rgba(255, 247, 230, 0.95) 0%, rgba(255, 255, 255, 0.78) 100%)',
    border: '1px solid rgba(197, 160, 40, 0.12)',
    boxShadow: '0 18px 36px rgba(197, 160, 40, 0.12)'
  },
  brandLogo: {
    width: '80%',
    height: '80%',
    objectFit: 'contain'
  },
  title: {
    fontSize: '32px',
    fontWeight: 800,
    letterSpacing: '-1px',
    lineHeight: 1
  },
  subtitle: {
    fontSize: '13px',
    fontWeight: 700,
    color: 'var(--accent-gold-dark)',
    fontFamily: 'Outfit',
    textTransform: 'uppercase',
    letterSpacing: '8px',
    textAlign: 'center',
    margin: 0
  },
  profileBtn: {
    width: '52px',
    height: '52px',
    borderRadius: '16px',
    background: 'rgba(255, 255, 255, 0.95)',
    border: '1px solid rgba(20, 20, 22, 0.06)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    cursor: 'pointer',
    boxShadow: '0 8px 16px rgba(20, 20, 22, 0.06)'
  }
}
