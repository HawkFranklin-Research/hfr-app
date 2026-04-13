import React from 'react'
import { Microscope, Laptop, Dna, HeartPulse } from 'lucide-react'

export default function Dashboard({ onProjectSelect }) {
  const projects = [
    { id: 'derma', name: 'Derma AI', icon: <Microscope size={28} color="#D9A441" /> },
    { id: 'tele', name: 'Telemedicine', icon: <Laptop size={28} color="#22D3EE" /> },
    { id: 'onco', name: 'Onco Tracker', icon: <Dna size={28} color="#f6d365" /> },
    { id: 'cardio', name: 'Cardio Health', icon: <HeartPulse size={28} color="#ff0844" /> }
  ]

  return (
    <main className="view-container">
      <h2 style={{ fontSize: '24px', margin: '10px 0 20px' }}>Active Projects</h2>
      <div style={styles.grid}>
        {projects.map(proj => (
          <div 
            key={proj.id} 
            className="glass-panel" 
            style={styles.tile}
            onClick={() => onProjectSelect(proj.name)}
          >
            <div style={styles.iconWrapper}>{proj.icon}</div>
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
    padding: '16px'
  },
  iconWrapper: {
    width: '60px', height: '60px',
    borderRadius: '16px',
    background: 'rgba(255, 255, 255, 0.05)',
    display: 'flex', alignItems: 'center', justifyContent: 'center'
  }
}
