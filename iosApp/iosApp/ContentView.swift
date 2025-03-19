//
//  ContentView.swift
//  iosApp
//
//  Created by Abdul Basit on 29/03/2024.
//

import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    var lifecycleRegister: LifecycleHolder

    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainiOS(lifecycle: lifecycleRegister.lifecycle)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var lifecycleRegister: LifecycleHolder
    var body: some View {
        ComposeView(lifecycleRegister:lifecycleRegister)
                .ignoresSafeArea() // Compose has own keyboard handler
    }
}


