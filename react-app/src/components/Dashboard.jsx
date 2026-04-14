import React from 'react'
import { Microscope, Laptop, Dna, HeartPulse } from 'lucide-react'

export default function Dashboard({ onProjectSelect }) {
  const projects = [
    {
      id: 'derma',
      name: 'Derma AI',
      tone: 'tile-accent-gold',
      iconBg: 'var(--surface-cream-strong)',
      icon: <Microscope size={28} color="#D9A441" />,
    },
    {
      id: 'tele',
      name: 'Telemedicine',
      tone: 'tile-accent-cyan',
      iconBg: 'var(--surface-cyan-strong)',
      icon: <Laptop size={28} color="#22D3EE" />,
    },
    {
      id: 'onco',
      name: 'Onco Tracker',
      tone: 'tile-accent-neutral',
      iconBg: 'var(--surface-neutral-strong)',
      icon: <Dna size={28} color="#A3B1C6" />,
    },
    {
      id: 'cardio',
      name: 'Cardio Health',
      tone: 'tile-accent-gold',
      iconBg: '#F7E8C5',
      icon: <HeartPulse size={28} color="#C5A028" />,
    }
  ]

  return (
    <main className="view-container">
      <h2 style={{ fontSize: '24px', margin: '10px 0 20px' }}>Active Projects</h2>
      <div style={styles.grid}>
        {projects.map(proj => (
          <div 
            key={proj.id} 
            className={`glass-panel ${proj.tone}`}
            style={styles.tile}
            onClick={() => onProjectSelect(proj.name)}
          >
            <div style={{ ...styles.iconWrapper, background: proj.iconBg }}>{proj.icon}</div>
            <h3 style={{ fontSize: '16px', fontWeight: 500 }}>{proj.name}</h3>
          </div>
        ))}
      </div>
    </main>
  )
}

const styles = {
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(2, 1fr)',
    gap: '16px'
  },
  tile: {
    aspectRatio: '1',
    display: 'flex', flexDirection: 'column',
    alignItems: 'center', justifyContent: 'center',
    gap: '16px', cursor: 'pointer', textAlign: 'center',
    padding: '16px',
    boxShadow: 'var(--shadow-card)'
  },
  iconWrapper: {
    width: '60px', height: '60px',
    borderRadius: '16px',
    border: '1px solid rgba(20, 20, 22, 0.04)',
    display: 'flex', alignItems: 'center', justifyContent: 'center'
  }
}
