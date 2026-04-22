import React, { useState, useEffect, useRef, useCallback } from 'react'
import { ArrowLeft, CheckCircle, Loader2, ChevronLeft, ChevronRight, ZoomIn, ZoomOut, RotateCcw } from 'lucide-react'
import { db, auth } from '../../config/firebase'
import { collection, getDocs, addDoc, serverTimestamp } from 'firebase/firestore'

export default function FlashcardEngine({ project, onExit }) {
  const [index, setIndex] = useState(0)
  const [cards, setCards] = useState([])
  const [loading, setLoading] = useState(true)
  const [isSuccessAnim, setIsSuccessAnim] = useState(false)
  
  // Image Viewer State
  const [activeImgIndex, setActiveImgIndex] = useState(0)
  const [zoom, setZoom] = useState(1)
  const [offset, setOffset] = useState({ x: 0, y: 0 })
  const [isDragging, setIsDragging] = useState(false)
  const [lastPos, setLastPos] = useState({ x: 0, y: 0 })
  
  const [probabilities, setProbabilities] = useState({})

  useEffect(() => {
    const fetchCases = async () => {
      try {
        const querySnapshot = await getDocs(collection(db, `projects/derma_ai/questionnaire`))
        const caseData = querySnapshot.docs.map(doc => ({
          id: doc.id,
          q: doc.data().prompt,
          desc: doc.data().helper,
          image: doc.data().imageUrl || null,
          images: doc.data().images || [], 
          options: doc.data().options || [],
          type: doc.data().type || 'multiple_choice'
        }))
        setCards(caseData.length ? caseData : [{ id: 'error', q: "No cases", desc: "Seed DB", options: [] }])
      } catch (err) {
        console.error("Error fetching cases:", err)
      } finally {
        setLoading(false)
      }
    }
    fetchCases()
  }, [project])

  // Pre-fetching Logic
  useEffect(() => {
    if (!cards[index]) return;
    const currentImages = cards[index].images || [];
    currentImages.forEach(url => {
      const img = new Image();
      img.src = url;
    });
    const nextCard = cards[index + 1];
    if (nextCard && nextCard.images && nextCard.images.length > 0) {
      const nextImg = new Image();
      nextImg.src = nextCard.images[0];
    }
  }, [index, cards]);

  const resetViewer = useCallback(() => {
    setZoom(1)
    setOffset({ x: 0, y: 0 })
    setIsDragging(false)
  }, [])

  useEffect(() => {
    setActiveImgIndex(0)
    resetViewer()
    if (cards[index]?.type === 'probability_grid') {
      const initial = {}
      cards[index].options.forEach(opt => initial[opt] = 0)
      setProbabilities(initial)
    }
  }, [index, cards, resetViewer])

  const handleStart = (e) => {
    if (zoom === 1) return
    setIsDragging(true)
    const touch = e.touches ? e.touches[0] : e
    setLastPos({ x: touch.clientX, y: touch.clientY })
  }

  const handleMove = (e) => {
    if (!isDragging || zoom === 1) return
    e.preventDefault() 
    const touch = e.touches ? e.touches[0] : e
    const deltaX = touch.clientX - lastPos.x
    const deltaY = touch.clientY - lastPos.y
    setOffset(prev => ({ x: prev.x + deltaX, y: prev.y + deltaY }))
    setLastPos({ x: touch.clientX, y: touch.clientY })
  }

  const handleEnd = () => setIsDragging(false)
  const handleZoomIn = () => setZoom(prev => Math.min(prev + 0.5, 4))
  const handleZoomOut = () => setZoom(prev => Math.max(prev - 0.5, 1))

  const handleNav = (dir) => {
    resetViewer()
    if (dir === 'next') setActiveImgIndex(prev => (prev === cards[index].images.length - 1 ? 0 : prev + 1))
    else setActiveImgIndex(prev => (prev === 0 ? cards[index].images.length - 1 : prev - 1))
  }

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
        projectId: 'derma_ai',
        questionId: cards[index].id || index,
        questionTitle: cards[index].q,
        answer: answerData,
        timestamp: serverTimestamp()
      })
    } catch (err) { console.error(err) }

    setTimeout(() => {
      setIsSuccessAnim(false)
      if (index < cards.length - 1) setIndex(prev => prev + 1)
      else { alert("Complete!"); onExit() }
    }, 800)
  }

  if (loading) return <div style={styles.center}><Loader2 className="animate-spin" size={40} color="var(--accent-cyan)" /></div>
  
  const card = cards[index]
  const currentImages = card.images.length > 0 ? card.images : (card.image ? [card.image] : [])

  return (
    <main className="view-container" style={{ padding: '20px 24px', overflowY: 'auto' }}>
      <div style={styles.header}>
        <button style={styles.backBtn} onClick={onExit}><ArrowLeft color="var(--text-main)"/></button>
        <div style={styles.pill}>Case {index + 1} / {cards.length}</div>
        <div style={{ width: '24px' }}></div>
      </div>

      <div className="glass-panel" style={styles.cardContainer}>
        
        <div 
          style={styles.viewerFrame}
          onMouseDown={handleStart}
          onMouseMove={handleMove}
          onMouseUp={handleEnd}
          onMouseLeave={handleEnd}
          onTouchStart={handleStart}
          onTouchMove={handleMove}
          onTouchEnd={handleEnd}
        >
          <div style={{
            ...styles.imageContainer,
            transform: `scale(${zoom}) translate(${offset.x / zoom}px, ${offset.y / zoom}px)`,
            cursor: zoom > 1 ? (isDragging ? 'grabbing' : 'grab') : 'default'
          }}>
            <img src={currentImages[activeImgIndex]} alt="Clinical View" style={styles.mainImage} draggable="false" />
          </div>
          
          {currentImages.length > 1 && zoom === 1 && (
            <div style={styles.navOverlay}>
              <button style={styles.sideBtn} onClick={() => handleNav('prev')}><ChevronLeft size={32} /></button>
              <button style={styles.sideBtn} onClick={() => handleNav('next')}><ChevronRight size={32} /></button>
            </div>
          )}

          <div style={styles.controlsRow}>
            <div style={styles.badge}>View {activeImgIndex + 1}/{currentImages.length}</div>
            <div style={styles.zoomGroup}>
              <button onClick={handleZoomOut} style={styles.zoomBtn}><ZoomOut size={18}/></button>
              <button onClick={handleZoomIn} style={styles.zoomBtn}><ZoomIn size={18}/></button>
              <button onClick={resetViewer} style={styles.zoomBtn}><RotateCcw size={18}/></button>
            </div>
          </div>
        </div>
        
        <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '16px 0 8px' }}>{card.q}</h3>
        <p style={{ color: 'var(--text-muted)', fontSize: '14px', marginBottom: '20px' }}>{card.desc}</p>
        
        {/* NEW: PROBABILITY SLIDERS */}
        <div style={styles.gridContainer}>
          <div style={{ ...styles.totalCounter, color: totalProb === 100 ? '#10B981' : '#F59E0B' }}>
             Total Study Probability: {totalProb}%
          </div>
          {card.options.map((opt, i) => (
            <div key={i} style={styles.sliderRow}>
              <div style={styles.sliderInfo}>
                <span style={styles.sliderLabel}>{opt}</span>
                <span style={styles.sliderVal}>{probabilities[opt] || 0}%</span>
              </div>
              <input 
                type="range" 
                min="0" max="100" step="1"
                value={probabilities[opt] || 0}
                onChange={(e) => handleProbChange(opt, e.target.value)}
                style={styles.sliderInput}
              />
            </div>
          ))}
          <button 
            className="button-primary" 
            style={styles.submitBtn}
            onClick={() => handleSelect(probabilities)}
            disabled={totalProb === 0}
          >
            Submit Professional Evaluation
          </button>
        </div>
      </div>
    </main>
  )
}

const styles = {
  center: { display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '80vh' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' },
  backBtn: { background: 'none', border: 'none', cursor: 'pointer' },
  pill: { background: 'var(--surface-cyan)', padding: '6px 16px', borderRadius: '20px', fontSize: '12px', fontWeight: 600, color: 'var(--accent-cyan-dark)' },
  cardContainer: { padding: '20px', background: '#FFF', borderRadius: '24px', boxShadow: 'var(--shadow-soft)' },
  viewerFrame: {
    width: '100%', height: '500px', maxHeight: '60vh', background: '#0F172A', borderRadius: '20px',
    position: 'relative', overflow: 'hidden', touchAction: 'none'
  },
  imageContainer: { width: '100%', height: '100%', transition: 'transform 0.1s ease-out' },
  mainImage: { width: '100%', height: '100%', objectFit: 'contain', userSelect: 'none' },
  navOverlay: {
    position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
    display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0 10px',
    pointerEvents: 'none'
  },
  sideBtn: {
    width: '50px', height: '50px', borderRadius: '50%', background: 'rgba(255,255,255,0.2)',
    border: 'none', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
    cursor: 'pointer', pointerEvents: 'auto', backdropFilter: 'blur(4px)'
  },
  controlsRow: {
    position: 'absolute', bottom: '12px', left: '12px', right: '12px',
    display: 'flex', justifyContent: 'space-between', alignItems: 'center', pointerEvents: 'none'
  },
  badge: { background: 'rgba(0,0,0,0.6)', color: '#fff', padding: '5px 12px', borderRadius: '12px', fontSize: '11px' },
  zoomGroup: { display: 'flex', gap: '8px', pointerEvents: 'auto' },
  zoomBtn: { 
    width: '36px', height: '36px', borderRadius: '10px', background: 'rgba(255,255,255,0.9)', 
    border: 'none', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
    boxShadow: '0 4px 8px rgba(0,0,0,0.2)'
  },
  gridContainer: { display: 'flex', flexDirection: 'column', gap: '8px' },
  sliderRow: { 
    display: 'flex', flexDirection: 'column', gap: '8px',
    padding: '14px', background: '#F8FAFC', borderRadius: '18px',
    border: '1px solid rgba(0,0,0,0.02)'
  },
  sliderInfo: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  sliderLabel: { fontSize: '14px', fontWeight: 600, color: '#334155' },
  sliderVal: { fontSize: '14px', fontWeight: 800, color: 'var(--accent-cyan-dark)' },
  sliderInput: {
    width: '100%', height: '6px', borderRadius: '5px',
    background: '#E2E8F0', outline: 'none', appearance: 'none',
    cursor: 'pointer'
  },
  totalCounter: { textAlign: 'right', fontSize: '14px', fontWeight: 800, marginBottom: '8px' },
  submitBtn: { marginTop: '20px', width: '100%', padding: '18px', borderRadius: '18px', fontWeight: 700, fontSize: '16px' },
  successOverlay: {
    position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
    background: 'rgba(255, 255, 255, 0.9)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    animation: 'fadeIn 0.2s', zIndex: 20, borderRadius: '24px'
  }
}
