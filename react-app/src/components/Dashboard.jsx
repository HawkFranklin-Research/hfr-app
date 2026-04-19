import React, { useState, useEffect } from 'react'
import { Microscope, Laptop, Dna, HeartPulse } from 'lucide-react'
import { db } from '../config/firebase'
import { collection, getDocs } from 'firebase/firestore'
import pelliscopeLogo from '../assets/pelliscope.png'

export default function Dashboard({ onProjectSelect }) {
  const [projects, setProjects] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const querySnapshot = await getDocs(collection(db, 'projects'))
        const projData = querySnapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.to_dict ? doc.to_dict() : doc.data()
        }))
        
        if (projData.length === 0) {
          setProjects([
            { id: 'derma_ai', name: 'Dermatology AI', tone: 'tile-accent-gold', iconBg: 'var(--surface-cream-strong)' }
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

  const renderIcon = (proj) => {
    if (proj.id === 'derma_ai' || proj.logoKey === 'derma') {
      return <img src={pelliscopeLogo} alt="Pelliscope" style={{ width: '40px', height: '40px', objectFit: 'contain' }} />
    }
    return <Microscope size={28} color="#D9A441" />
  }

  if (loading) return <main className="view-container"><p>Loading projects...</p></main>

  return (
    <main className="view-container">
      <h2 style={{ fontSize: '24px', margin: '10px 0 20px', fontWeight: 700 }}>Active Projects</h2>
      <div style={styles.grid}>
        {projects.map(proj => (
          <div 
            key={proj.id} 
            className={`glass-panel ${proj.tone || 'tile-accent-gold'}`}
            style={styles.tile}
            onClick={() => onProjectSelect(proj.name || proj.id)}
          >
            <div style={{ ...styles.iconWrapper, background: proj.iconBg || 'var(--surface-cream-strong)' }}>
              {renderIcon(proj)}
            </div>
            <h3 style={{ fontSize: '18px', fontWeight: 700, marginTop: '8px' }}>{proj.name}</h3>
            <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginTop: '4px' }}>{proj.shortDescription || 'Clinical Study'}</p>
          </div>
        ))}
      </div>
    </main>
  )
}

const styles = {
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(1, 1fr)', // One large tile looks better when there is only one project
    gap: '20px',
    maxWidth: '400px',
    margin: '0 auto'
  },
  tile: {
    aspectRatio: 'auto',
    minHeight: '200px',
    display: 'flex', flexDirection: 'column',
    alignItems: 'center', justifyContent: 'center',
    gap: '12px', cursor: 'pointer', textAlign: 'center',
    padding: '32px 24px',
    boxShadow: '0 20px 40px rgba(0,0,0,0.06)',
    borderRadius: '24px'
  },
  iconWrapper: {
    width: '80px', height: '80px',
    borderRadius: '22px',
    border: '1px solid rgba(197, 160, 40, 0.1)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    marginBottom: '8px'
  }
}
