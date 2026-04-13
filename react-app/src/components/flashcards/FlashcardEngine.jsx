import React, { useState } from 'react'
import { ArrowLeft, CheckCircle } from 'lucide-react'

export default function FlashcardEngine({ project, onExit }) {
  const [index, setIndex] = useState(0)
  const [isSuccessAnim, setIsSuccessAnim] = useState(false)

  const cards = [
    {
      q: "Patient presents with asymptomatic irregular macular lesion on the right forearm.",
      desc: "Dimensions: 8mm x 6mm. Borders are distinct but irregular. Color variation present. What is the most likely initial step in management?",
      image: "https://images.unsplash.com/photo-1579684385127-1ef15d508118?q=80&w=800&auto=format&fit=crop",
      options: ['Reassurance and observation', 'Cryotherapy', 'Excisional biopsy', 'Topical fluorouracil']
    },
    {
      q: "32-year-old male with sudden onset chest pain radiating to left arm.",
      desc: "Patient appears diaphoretic. ECG shows ST elevation in leads II, III, aVF. What is the diagnosis?",
      image: null,
      options: ['Anterior MI', 'Inferior MI', 'Pericarditis', 'Pulmonary Embolism']
    }
  ]

  const handleSelect = (idx) => {
    setIsSuccessAnim(true)
    setTimeout(() => {
      setIsSuccessAnim(false)
      if (index < cards.length - 1) {
        setIndex(prev => prev + 1)
      } else {
        alert("Project data collection complete!")
        onExit()
      }
    }, 800)
  }

  const card = cards[index]

  return (
    <main className="view-container" style={{ padding: '20px 24px' }}>
      <div style={styles.header}>
        <button style={styles.backBtn} onClick={onExit}><ArrowLeft color="var(--text-main)"/></button>
        <div style={styles.pill}>Case {index + 1} / {cards.length}</div>
        <div style={{ width: '24px' }}></div>
      </div>

      <div className="glass-panel" style={styles.cardContainer}>
        {card.image && (
          <div style={styles.imageWrapper}>
            <img src={card.image} alt="Case visual" style={styles.image} />
          </div>
        )}
        
        <h3 style={{ fontSize: '20px', lineHeight: 1.4, marginBottom: '12px' }}>{card.q}</h3>
        <p style={{ color: 'var(--text-muted)', lineHeight: 1.6, fontSize: '15px', marginBottom: '24px' }}>{card.desc}</p>
        
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginTop: 'auto' }}>
          {card.options.map((opt, i) => (
            <button key={i} style={styles.optionBtn} onClick={() => handleSelect(i)}>
              <div style={styles.optionLabel}>{String.fromCharCode(65 + i)}</div>
              <span>{opt}</span>
            </button>
          ))}
        </div>

        {isSuccessAnim && (
          <div style={styles.successOverlay}>
            <CheckCircle size={80} color="var(--accent-cyan)" />
          </div>
        )}
      </div>
    </main>
  )
}

const styles = {
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
  backBtn: { background: 'none', border: 'none', cursor: 'pointer' },
  pill: { background: 'rgba(255,255,255,0.1)', padding: '6px 16px', borderRadius: '20px', fontSize: '12px', fontWeight: 600, color: 'var(--accent-cyan)', fontFamily: 'Inter' },
  cardContainer: { flex: 1, display: 'flex', flexDirection: 'column', padding: '24px', position: 'relative', overflow: 'hidden' },
  imageWrapper: { width: '100%', height: '220px', borderRadius: '16px', overflow: 'hidden', marginBottom: '20px' },
  image: { width: '100%', height: '100%', objectFit: 'cover' },
  optionBtn: { 
    display: 'flex', alignItems: 'center', padding: '16px', borderRadius: '16px',
    background: 'rgba(255,255,255,0.05)', border: '1px solid var(--glass-border)',
    color: 'var(--text-main)', fontSize: '15px', fontWeight: 500, cursor: 'pointer',
    textAlign: 'left', fontFamily: 'Inter'
  },
  optionLabel: {
    width: '28px', height: '28px', borderRadius: '8px', background: 'rgba(255,255,255,0.1)',
    display: 'flex', alignItems: 'center', justifyContent: 'center', marginRight: '16px',
    fontFamily: 'Outfit', fontWeight: 700, color: 'var(--accent-gold)'
  },
  successOverlay: {
    position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
    background: 'linear-gradient(135deg, rgba(34, 211, 238, 0.2) 0%, transparent 100%)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    animation: 'fadeIn 0.2s'
  }
}
