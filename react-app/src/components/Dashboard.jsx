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

  const renderIcon = (proj) => {
    if (proj.id === 'derma_ai' || proj.logoKey === 'derma') {
      return <img src={pelliscopeLogo} alt="Pelliscope" style={{ width: '40px', height: '40px', objectFit: 'contain' }} />
    }
    return <Microscope size={28} color="#D9A441" />
  }

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
      <section style={styles.intro}>
        <p style={styles.kicker}>Active project</p>
        <h2 style={styles.heading}>Clinical AI screening, centered for mobile-first review.</h2>
        <p style={styles.copy}>
          Select the live HawkFranklin study below to open the Dermatology AI evaluation flow.
        </p>
      </section>

      <div style={styles.grid}>
        {projects.map(proj => (
          <button
            type="button"
            key={proj.id} 
            className={`glass-panel ${proj.tone || 'tile-accent-gold'}`}
            style={styles.tile}
            onClick={() => onProjectSelect(proj.name || proj.id)}
          >
            <div style={styles.tileTop}>
              <p style={styles.projectEyebrow}>Project</p>
              <h3 style={styles.projectTitle}>{proj.name}</h3>
              <p style={styles.projectCopy}>
                {proj.shortDescription || 'Clinical study validation workflow for dermatologist-led review.'}
              </p>
            </div>

            <div style={styles.banner}>
              <div style={{ ...styles.iconWrapper, background: proj.iconBg || 'var(--surface-cream-strong)' }}>
                {renderIcon(proj)}
              </div>
              <div style={styles.bannerText}>
                <span style={styles.bannerLabel}>Powered by</span>
                <span style={styles.bannerTitle}>Pelliscope</span>
              </div>
            </div>

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
    margin: '0 auto'
  },
  intro: {
    width: '100%',
    maxWidth: '420px',
    margin: '0 auto 20px',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    textAlign: 'center',
    gap: '10px'
  },
  kicker: {
    fontSize: '12px',
    fontWeight: 700,
    letterSpacing: '0.24em',
    textTransform: 'uppercase',
    color: 'var(--accent-gold-dark)'
  },
  heading: {
    fontSize: '28px',
    lineHeight: 1.08,
    letterSpacing: '-0.03em'
  },
  copy: {
    fontSize: '14px',
    lineHeight: 1.6,
    color: 'var(--text-muted)'
  },
  tile: {
    minHeight: '260px',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'stretch',
    justifyContent: 'space-between',
    gap: '18px',
    cursor: 'pointer',
    textAlign: 'left',
    padding: '28px 24px',
    boxShadow: '0 20px 40px rgba(0,0,0,0.06)',
    borderRadius: '28px',
    border: '1px solid rgba(20, 20, 22, 0.04)',
    appearance: 'none'
  },
  tileTop: {
    display: 'flex',
    flexDirection: 'column',
    gap: '8px'
  },
  projectEyebrow: {
    fontSize: '11px',
    fontWeight: 700,
    letterSpacing: '0.18em',
    textTransform: 'uppercase',
    color: 'var(--text-muted)'
  },
  projectTitle: {
    fontSize: '30px',
    lineHeight: 1,
    letterSpacing: '-0.04em'
  },
  projectCopy: {
    fontSize: '14px',
    lineHeight: 1.55,
    color: 'var(--text-soft)'
  },
  banner: {
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
    padding: '16px 18px',
    borderRadius: '22px',
    background: 'rgba(255, 255, 255, 0.7)',
    border: '1px solid rgba(20, 20, 22, 0.05)'
  },
  iconWrapper: {
    width: '72px',
    height: '72px',
    borderRadius: '20px',
    border: '1px solid rgba(197, 160, 40, 0.1)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    flexShrink: 0
  },
  bannerText: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px'
  },
  bannerLabel: {
    fontSize: '11px',
    fontWeight: 700,
    letterSpacing: '0.18em',
    textTransform: 'uppercase',
    color: 'var(--text-muted)'
  },
  bannerTitle: {
    fontSize: '24px',
    fontFamily: 'Outfit, sans-serif',
    fontWeight: 700,
    lineHeight: 1,
    color: 'var(--text-main)'
  },
  tileFooter: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: '2px'
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
    paddingTop: '12px',
    textAlign: 'center'
  },
  loadingText: {
    fontSize: '14px',
    color: 'var(--text-muted)'
  }
}
