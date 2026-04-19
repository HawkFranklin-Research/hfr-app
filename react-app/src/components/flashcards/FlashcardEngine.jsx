import React, { useState, useEffect } from 'react'
import { ArrowLeft, CheckCircle, Loader2, Info } from 'lucide-react'
import { db, auth } from '../../config/firebase'
import { collection, getDocs, addDoc, serverTimestamp, query, where } from 'firebase/firestore'

export default function FlashcardEngine({ project, onExit }) {
  const [index, setIndex] = useState(0)
  const [cards, setCards] = useState([])
  const [loading, setLoading] = useState(true)
  const [isSuccessAnim, setIsSuccessAnim] = useState(false)
  const [probabilities, setProbabilities] = useState({}) // For the new grid type

  useEffect(() => {
    const fetchCases = async () => {
      try {
        // Find project document to get its ID if project passed is just a name
        const projectsSnap = await getDocs(collection(db, 'projects'))
        const projDoc = projectsSnap.docs.find(d => d.data().name === project || d.id === project)
        const projId = projDoc ? projDoc.id : 'derma_ai'

        const querySnapshot = await getDocs(collection(db, `projects/${projId}/questionnaire`))
        const caseData = querySnapshot.docs.map(doc => ({
          id: doc.id,
          q: doc.data().prompt,
          desc: doc.data().helper,
          image: doc.data().imageUrl || null,
          images: doc.data().images || [], // For multi-view cases
          options: doc.data().options || [],
          type: doc.data().type || 'multiple_choice'
        }))
        
        if (caseData.length === 0) {
          setCards([{
            id: 'error', q: "No cases found", desc: "Please seed the database.", options: []
          }])
        } else {
          setCards(caseData)
        }
      } catch (err) {
        console.error("Error fetching cases:", err)
      } finally {
        setLoading(false)
      }
    }
    fetchCases()
  }, [project])

  // Reset probabilities when index changes
  useEffect(() => {
    if (cards[index]?.type === 'probability_grid') {
      const initial = {}
      cards[index].options.forEach(opt => initial[opt] = 0)
      setProbabilities(initial)
    }
  }, [index, cards])

  const handleProbChange = (disease, val) => {
    setProbabilities(prev => ({ ...prev, [disease]: parseInt(val) || 0 }))
  }

  const totalProb = Object.values(probabilities).reduce((a, b) => a + b, 0)

  const handleSelect = async (answerData) => {
    setIsSuccessAnim(true)
    
    try {
      await addDoc(collection(db, 'responses'), {
        uid: auth.currentUser?.uid,
        userEmail: auth.currentUser?.email,
        projectId: project,
        questionId: cards[index].id || index,
        questionTitle: cards[index].q,
        answer: answerData,
        timestamp: serverTimestamp()
      })
    } catch (err) {
      console.error("Error saving response:", err)
    }

    setTimeout(() => {
      setIsSuccessAnim(false)
      if (index < cards.length - 1) {
        setIndex(prev => prev + 1)
      } else {
        alert("Evaluation complete! Thank you for your contribution.")
        onExit()
      }
    }, 800)
  }

  if (loading) return (
    <main className="view-container" style={styles.center}>
      <Loader2 className="animate-spin" size={40} color="var(--accent-cyan)"/>
      <p style={{ marginTop: '16px', color: 'var(--text-muted)' }}>Loading cases...</p>
    </main>
  )
  
  const card = cards[index]

  return (
    <main className="view-container" style={{ padding: '20px 24px', overflowY: 'auto' }}>
      <div style={styles.header}>
        <button style={styles.backBtn} onClick={onExit}><ArrowLeft color="var(--text-main)"/></button>
        <div style={styles.pill}>Case {index + 1} / {cards.length}</div>
        <div style={{ width: '24px' }}></div>
      </div>

      <div className="glass-panel" style={styles.cardContainer}>
        {/* IMAGE VIEWS */}
        {card.images && card.images.length > 0 ? (
          <div style={styles.multiImageContainer}>
            {card.images.map((img, i) => (
              <div key={i} style={styles.smallImageWrapper}>
                <img src={img} alt={`View ${i+1}`} style={styles.image} />
                <span style={styles.imageLabel}>View {i+1}</span>
              </div>
            ))}
          </div>
        ) : card.image && (
          <div style={styles.imageWrapper}>
            <img src={card.image} alt="Case visual" style={styles.image} />
          </div>
        )}
        
        <h3 style={{ fontSize: '18px', lineHeight: 1.4, marginBottom: '8px' }}>{card.q}</h3>
        <p style={{ color: 'var(--text-muted)', lineHeight: 1.5, fontSize: '14px', marginBottom: '20px' }}>{card.desc}</p>
        
        {/* QUESTION TYPE LOGIC */}
        {card.type === 'probability_grid' ? (
          <div style={styles.gridContainer}>
            <div style={{ ...styles.totalCounter, color: totalProb === 100 ? '#10B981' : 'var(--text-muted)' }}>
               Total Probability: {totalProb}%
            </div>
            {card.options.map((opt, i) => (
              <div key={i} style={styles.gridRow}>
                <span style={styles.gridLabel}>{opt}</span>
                <input 
                  type="number" 
                  min="0" max="100"
                  value={probabilities[opt]}
                  onChange={(e) => handleProbChange(opt, e.target.value)}
                  style={styles.gridInput}
                />
                <span style={{ fontSize: '14px', marginLeft: '4px' }}>%</span>
              </div>
            ))}
            <button 
              className="button-primary" 
              style={{ marginTop: '20px', width: '100%', padding: '14px' }}
              onClick={() => handleSelect(probabilities)}
              disabled={totalProb === 0}
            >
              Submit Probabilities
            </button>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginTop: 'auto' }}>
            {card.options.map((opt, i) => (
              <button key={i} style={styles.optionBtn} onClick={() => handleSelect(opt)}>
                <div style={styles.optionLabel}>{String.fromCharCode(65 + i)}</div>
                <span>{opt}</span>
              </button>
            ))}
          </div>
        )}

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
  center: { display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '60vh' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
  backBtn: { background: 'none', border: 'none', cursor: 'pointer' },
  pill: {
    background: 'var(--surface-cyan)',
    padding: '6px 16px',
    borderRadius: '20px',
    fontSize: '12px',
    fontWeight: 600,
    color: 'var(--accent-cyan-dark)',
    fontFamily: 'Inter',
    border: '1px solid rgba(34, 211, 238, 0.18)'
  },
  cardContainer: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    padding: '24px',
    position: 'relative',
    background: 'linear-gradient(180deg, #FFFFFF 0%, #FBFBFC 100%)',
    boxShadow: 'var(--shadow-soft)',
    minHeight: 'fit-content'
  },
  imageWrapper: { width: '100%', height: '220px', borderRadius: '16px', overflow: 'hidden', marginBottom: '20px' },
  multiImageContainer: { display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '10px', marginBottom: '20px' },
  smallImageWrapper: { position: 'relative', height: '100px', borderRadius: '12px', overflow: 'hidden', border: '1px solid var(--glass-border)' },
  imageLabel: { position: 'absolute', bottom: '4px', left: '4px', background: 'rgba(0,0,0,0.5)', color: 'white', fontSize: '10px', padding: '2px 6px', borderRadius: '4px' },
  image: { width: '100%', height: '100%', objectFit: 'cover' },
  optionBtn: { 
    display: 'flex', alignItems: 'center', padding: '16px', borderRadius: '16px',
    background: 'var(--surface-neutral)', border: '1px solid var(--glass-border)',
    color: 'var(--text-main)', fontSize: '15px', fontWeight: 500, cursor: 'pointer',
    textAlign: 'left', fontFamily: 'Inter'
  },
  optionLabel: {
    width: '28px', height: '28px', borderRadius: '8px', background: 'var(--surface-cream)',
    display: 'flex', alignItems: 'center', justifyContent: 'center', marginRight: '16px',
    fontFamily: 'Outfit', fontWeight: 700, color: 'var(--accent-gold)'
  },
  gridContainer: { display: 'flex', flexDirection: 'column', gap: '8px' },
  gridRow: { display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 12px', background: 'var(--surface-neutral)', borderRadius: '12px' },
  gridLabel: { fontSize: '14px', flex: 1, fontWeight: 500 },
  gridInput: { width: '60px', padding: '8px', borderRadius: '8px', border: '1px solid var(--glass-border)', textAlign: 'right', fontFamily: 'Inter', fontWeight: 600 },
  totalCounter: { textAlign: 'right', fontSize: '13px', fontWeight: 700, marginBottom: '8px' },
  successOverlay: {
    position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
    background: 'linear-gradient(135deg, rgba(34, 211, 238, 0.16) 0%, rgba(255, 255, 255, 0.78) 100%)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    animation: 'fadeIn 0.2s', zIndex: 10
  }
}
