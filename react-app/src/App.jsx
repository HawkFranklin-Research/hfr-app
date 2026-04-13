import React, { useState } from 'react'
import TopBar from './components/TopBar'
import Dashboard from './components/Dashboard'
import AuthPanel from './components/AuthPanel'
import ConsentModal from './components/ConsentModal'
import FlashcardEngine from './components/flashcards/FlashcardEngine'

function App() {
  const [view, setView] = useState('dashboard') // 'dashboard' | 'flashcards'
  const [activeProject, setActiveProject] = useState(null)
  
  const [isAuthOpen, setIsAuthOpen] = useState(false)
  const [isConsentOpen, setIsConsentOpen] = useState(false)
  const [pendingProject, setPendingProject] = useState(null)
  
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  const handleProjectClick = (project) => {
    setPendingProject(project)
    setIsConsentOpen(true)
  }

  const handleConsentAgreed = () => {
    setIsConsentOpen(false)
    if (!isAuthenticated) {
      setIsAuthOpen(true)
    } else {
      setActiveProject(pendingProject)
      setView('flashcards')
    }
  }

  return (
    <>
      {view === 'dashboard' && (
        <>
          <TopBar onProfileClick={() => setIsAuthOpen(true)} isAuthenticated={isAuthenticated} />
          <Dashboard onProjectSelect={handleProjectClick} />
        </>
      )}

      {view === 'flashcards' && (
        <FlashcardEngine 
          project={activeProject} 
          onExit={() => setView('dashboard')} 
        />
      )}

      {isConsentOpen && (
        <ConsentModal 
          project={pendingProject}
          onAgree={handleConsentAgreed}
          onCancel={() => setIsConsentOpen(false)}
        />
      )}

      <AuthPanel 
        isOpen={isAuthOpen} 
        onClose={() => setIsAuthOpen(false)}
        isAuthenticated={isAuthenticated}
        onLogin={() => setIsAuthenticated(true)}
        onLogout={() => {
          setIsAuthenticated(false)
          setView('dashboard')
        }}
      />
    </>
  )
}

export default App
