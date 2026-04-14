import React from 'react'
import { FileSignature } from 'lucide-react'

export default function ConsentModal({ project, onAgree, onCancel }) {
  return (
    <div style={styles.overlay}>
      <div className="glass-panel" style={styles.content}>
        <FileSignature size={48} color="var(--accent-cyan)" style={{ marginBottom: '20px' }} />
        <h2 style={{ fontSize: '24px', marginBottom: '12px' }}>{project} Consent</h2>
        <p style={{ fontSize: '14px', color: 'var(--text-muted)', lineHeight: 1.5, marginBottom: '32px' }}>
          By continuing, you agree to participate in this data collection study. 
          Your responses will be recorded securely and anonymized for research purposes.
        </p>
        <button className="btn-primary" style={{ marginBottom: '12px' }} onClick={onAgree}>I Agree, Continue</button>
        <button className="btn-secondary" onClick={onCancel}>Cancel</button>
      </div>
    </div>
  )
}

const styles = {
  overlay: {
    position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
    background: 'rgba(20, 20, 22, 0.16)', backdropFilter: 'blur(8px)',
    zIndex: 100, display: 'flex', alignItems: 'center', justifyContent: 'center',
    padding: '24px', animation: 'fadeIn 0.2s'
  },
  content: {
    width: '100%', maxWidth: '400px', padding: '32px 24px', textAlign: 'center',
    background: 'linear-gradient(180deg, #FFFFFF 0%, #FBFBFC 100%)',
    boxShadow: 'var(--shadow-soft)',
    animation: 'scaleUp 0.3s cubic-bezier(0.16, 1, 0.3, 1)'
  }
}
