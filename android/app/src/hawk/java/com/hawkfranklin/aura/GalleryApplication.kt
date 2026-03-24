package com.hawkfranklin.aura

import android.app.Application
import com.google.firebase.FirebaseApp

class GalleryApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    runCatching { FirebaseApp.initializeApp(this) }
  }
}
