import React, { useState, useEffect } from 'react'
import { Microscope, Laptop, Dna, HeartPulse } from 'lucide-react'
import { db } from '../config/firebase'
import { collection, getDocs } from 'firebase/firestore'

export default function Dashboard({ onProjectSelect }) {
  const [projects, setProjects] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const querySnapshot = await getDocs(collection(db, 'projects'))
        const projData = querySnapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.to_dict ? doc.to_dict() : doc.data(), // handle different SDK styles
          icon: getIcon(doc.data().logoKey || doc.id)
        }))
        
        if (projData.length === 0) {
          // Fallback if DB is empty
          setProjects([
            { id: 'derma', name: 'Derma AI', tone: 'tile-accent-gold', iconBg: 'var(--surface-cream-strong)', icon: <Microscope size={28} color="#D9A441" /> },
            { id: 'tele', name: 'Telemedicine', tone: 'tile-accent-cyan', iconBg: 'var(--surface-cyan-strong)', icon: <Laptop size={28} color="#22D3EE" /> }
          ])
        } else {
          setProjects(projData)
        }
      } catch (err) {
        console.error("Error fetching projects:", err)
      } finally {
        setLoading(false)
      }
    }
    fetchProjects()
  }, [])

  const getIcon = (key) => {
    switch (key) {
      case 'derma': return <Microscope size={28} color="#D9A441" />
      case 'telemedicine': case 'tele': return <Laptop size={28} color="#22D3EE" />
      case 'onco': return <Dna size={28} color="#A3B1C6" />
      case 'cardio': return <HeartPulse size={28} color="#C5A028" />
      default: return <Microscope size={28} />
    }
  }

  if (loading) return <main className="view-container"><p>Loading projects...</p></main>

  return (
    <main className="view-container">
      <h2 style={{ fontSize: '24px', margin: '10px 0 20px' }}>Active Projects</h2>
      <div style={styles.grid}>
        {projects.map(proj => (
          <div 
            key={proj.id} 
            className={`glass-panel ${proj.tone || 'tile-accent-neutral'}`}
            style={styles.tile}
            onClick={() => onProjectSelect(proj.name || proj.id)}
          >
            <div style={{ ...styles.iconWrapper, background: proj.iconBg || 'var(--surface-neutral-strong)' }}>{proj.icon}</div>
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
