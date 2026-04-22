import React, { useState, useEffect } from 'react'
import { Microscope, ArrowRight } from 'lucide-react'
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

  if (loading) {
    return (
      <main className="view-container dashboard-view">
        <div style={styles.loadingWrap}>
          <p style={styles.loadingText}>Loading projects...</p>
        </div>
      </main>
    )
  }

  return (
    <main className="view-container dashboard-view">
      <div style={styles.grid}>
        {projects.map(proj => (
          <button
            type="button"
            key={proj.id} 
            className={`glass-panel ${proj.tone || 'tile-accent-gold'}`}
            style={styles.tile}
            onClick={() => onProjectSelect(proj.name || proj.id)}
          >
            <div style={styles.tileMain}>
              <div style={{ ...styles.iconWrapper, background: proj.iconBg || 'var(--surface-cream-strong)' }}>
                 <img src={pelliscopeLogo} alt="Pelliscope" style={{ width: '40px', height: '40px', objectFit: 'contain' }} />
              </div>
              <div style={styles.tileText}>
                <p style={styles.projectEyebrow}>Active Study</p>
                <h3 style={styles.projectTitle}>{proj.name}</h3>
              </div>
            </div>

            <p style={styles.projectCopy}>
              Clinical Validation of 10 Dermatological Conditions
            </p>

            <div style={styles.tileFooter}>
              <span style={styles.tileAction}>Open evaluation flow</span>
              <ArrowRight size={18} color="var(--text-dark)" />
            </div>
          </button>
        ))}
      </div>
    </main>
  )
}

const styles = {
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(1, minmax(0, 1fr))',
    gap: '20px',
    width: '100%',
    maxWidth: '420px',
    margin: '40px auto 0' // Added margin top to replace the intro space
  },
  tile: {
    minHeight: '220px',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'stretch',
    justifyContent: 'space-between',
    gap: '24px',
    cursor: 'pointer',
    textAlign: 'left',
    padding: '32px 24px',
    boxShadow: '0 20px 40px rgba(0,0,0,0.06)',
    borderRadius: '28px',
    border: '1px solid rgba(20, 20, 22, 0.04)',
    appearance: 'none',
    width: '100%'
  },
  tileMain: {
    display: 'flex',
    alignItems: 'center',
    gap: '18px'
  },
  iconWrapper: {
    width: '72px',
    height: '72px',
    borderRadius: '20px',
    border: '1px solid rgba(197, 160, 40, 0.1)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    flexShrink: 0
  },
  tileText: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px'
  },
  projectEyebrow: {
    fontSize: '11px',
    fontWeight: 700,
    letterSpacing: '0.18em',
    textTransform: 'uppercase',
    color: 'var(--accent-gold-dark)'
  },
  projectTitle: {
    fontSize: '28px',
    lineHeight: 1,
    letterSpacing: '-0.04em'
  },
  projectCopy: {
    fontSize: '15px',
    lineHeight: 1.5,
    color: 'var(--text-soft)',
    fontWeight: 500
  },
  tileFooter: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: '12px',
    borderTop: '1px solid rgba(20, 20, 22, 0.04)'
  },
  tileAction: {
    fontSize: '14px',
    fontWeight: 700,
    color: 'var(--text-dark)'
  },
  loadingWrap: {
    width: '100%',
    maxWidth: '420px',
    margin: '0 auto',
    paddingTop: '100px',
    textAlign: 'center'
  },
  loadingText: {
    fontSize: '14px',
    color: 'var(--text-muted)'
  }
}
