import SwiftUI

struct CreatePostView: View {
    @Environment(\.managedObjectContext) private var viewContext
    @Environment(\.presentationMode) var presentationMode
    
    @State private var content: String = ""
    @State private var isPublic: Bool = false
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("What's on your mind?")) {
                    TextEditor(text: $content)
                        .frame(minHeight: 150)
                }
                
                Section {
                    Toggle("Make Public", isOn: $isPublic)
                }
                
                Section(footer: Text(isPublic ? "Visible to everyone" : "Private diary entry")) {
                    EmptyView()
                }
            }
            .navigationTitle("New Post")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Post") {
                        savePost()
                    }
                    .disabled(content.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                }
            }
        }
    }
    
    private func savePost() {
        let newPost = Post(context: viewContext)
        newPost.id = UUID()
        newPost.content = content
        newPost.timestamp = Date()
        newPost.isPublic = isPublic
        
        // AI Analysis using PyTorch MobileBERT Agent
        let stressScore = StressAnalysisAgent.shared.analyzeText(content)
        newPost.stressScore = Float(stressScore)
        newPost.sentiment = stressScore > 50 ? "Stressed" : "Normal"
        
        do {
            try viewContext.save()
            presentationMode.wrappedValue.dismiss()
        } catch {
            let nsError = error as NSError
            fatalError("Unresolved error \(nsError), \(nsError.userInfo)")
        }
    }
}
