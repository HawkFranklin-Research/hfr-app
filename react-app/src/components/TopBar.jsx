import React from 'react'
import { User, UserCheck } from 'lucide-react'

export default function TopBar({ onProfileClick, isAuthenticated }) {
  return (
    <header style={styles.header}>
      <div style={styles.brand}>
        <div style={styles.brandIcon}>HF</div>
        <div>
          <h1 style={styles.title}>HawkFranklin</h1>
          <p style={styles.subtitle}>Research Network</p>
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
  brandIcon: {
    width: '40px', height: '40px',
    background: 'linear-gradient(135deg, var(--accent-gold), var(--accent-gold-dark))',
    borderRadius: '12px',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    fontWeight: '800', fontSize: '20px', color: 'var(--text-dark)'
  },
  title: {
    fontSize: '20px', letterSpacing: '0.5px'
  },
  subtitle: {
    fontSize: '12px', color: 'var(--accent-cyan)',
    fontFamily: 'Inter', textTransform: 'uppercase', letterSpacing: '1px'
  },
  profileBtn: {
    width: '44px', height: '44px',
    borderRadius: '50%',
    background: 'var(--glass-bg)',
    border: '1px solid var(--glass-border)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    cursor: 'pointer'
  }
}
