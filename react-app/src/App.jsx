import React, { useState, useEffect } from 'react'
import TopBar from './components/TopBar'
import Dashboard from './components/Dashboard'
import AuthPanel from './components/AuthPanel'
import ConsentModal from './components/ConsentModal'
import FlashcardEngine from './components/flashcards/FlashcardEngine'
import { auth } from './config/firebase'
import { onAuthStateChanged } from 'firebase/auth'

function App() {
  const [view, setView] = useState('dashboard') // 'dashboard' | 'flashcards'
  const [activeProject, setActiveProject] = useState(null)
  
  const [isAuthOpen, setIsAuthOpen] = useState(false)
  const [authPanelView, setAuthPanelView] = useState('menu')
  const [isConsentOpen, setIsConsentOpen] = useState(false)
  const [pendingProject, setPendingProject] = useState(null)
  
  const [user, setUser] = useState(null)

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      setUser(currentUser)
    })
    return () => unsubscribe()
  }, [])

  const isAuthenticated = !!user

  const handleProjectClick = (project) => {
    setPendingProject(project)
    setIsConsentOpen(true)
  }

  const handleConsentAgreed = () => {
    setIsConsentOpen(false)
    if (!isAuthenticated) {
      setAuthPanelView('auth') // Direct to Login/Register
      setIsAuthOpen(true)
    } else {
      setActiveProject(pendingProject)
      setView('flashcards')
    }
  }

  const openSidebar = (mode = 'menu') => {
    setAuthPanelView(mode)
    setIsAuthOpen(true)
  }

  return (
    <>
      {view === 'dashboard' && (
        <div className="home-screen-shell">
          <div className="home-shell glass-panel">
          <TopBar onProfileClick={() => openSidebar('menu')} isAuthenticated={isAuthenticated} />
          <Dashboard onProjectSelect={handleProjectClick} />
          </div>
        </div>
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
        initialView={authPanelView}
        onLogin={() => {
          // If a project was pending, start it after successful login
          if (pendingProject) {
            setActiveProject(pendingProject)
            setView('flashcards')
            setPendingProject(null)
          }
        }}
        onLogout={() => {
          setView('dashboard')
        }}
      />
    </>
  )
}

export default App
