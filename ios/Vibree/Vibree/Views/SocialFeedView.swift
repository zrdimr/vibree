import SwiftUI
import CoreData

struct SocialFeedView: View {
    @Environment(\.managedObjectContext) private var viewContext
    
    @FetchRequest(
        sortDescriptors: [NSSortDescriptor(keyPath: \Post.timestamp, ascending: false)],
        animation: .default)
    private var posts: FetchedResults<Post>
    
    @State private var showingCreatePost = false
    
    var body: some View {
        NavigationView {
            ZStack {
                List {
                    ForEach(posts) { post in
                        PostRow(post: post)
                    }
                    .onDelete(perform: deleteItems)
                }
                .listStyle(PlainListStyle())
                
                // Floating Action Button
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        Button(action: {
                            showingCreatePost = true
                        }) {
                            Image(systemName: "plus")
                                .font(.title.weight(.semibold))
                                .padding()
                                .background(Color.pink)
                                .foregroundColor(.white)
                                .clipShape(Circle())
                                .shadow(radius: 4, x: 0, y: 4)
                        }
                        .padding()
                    }
                }
            }
            .navigationTitle("Social Feed")
            .sheet(isPresented: $showingCreatePost) {
                CreatePostView()
            }
        }
    }
    
    private func deleteItems(offsets: IndexSet) {
        withAnimation {
            offsets.map { posts[$0] }.forEach(viewContext.delete)
            
            do {
                try viewContext.save()
            } catch {
                let nsError = error as NSError
                fatalError("Unresolved error \(nsError), \(nsError.userInfo)")
            }
        }
    }
}

struct PostRow: View {
    let post: Post
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(post.isPublic ? "Public Post" : "Private Diary")
                    .font(.caption)
                    .foregroundColor(.blue)
                    .padding(4)
                    .background(Color.blue.opacity(0.1))
                    .cornerRadius(4)
                
                Spacer()
                
                if let date = post.timestamp {
                    Text(date, style: .time)
                        .font(.caption)
                        .foregroundColor(.gray)
                }
            }
            
            Text(post.content ?? "")
                .font(.body)
                .padding(.vertical, 4)
            
            HStack {
                Spacer()
                if post.stressScore > 0 {
                    Text("Stress: \(Int(post.stressScore))")
                        .font(.caption)
                        .foregroundColor(post.stressScore > 70 ? .red : .green)
                }
            }
        }
        .padding(.vertical, 8)
    }
}
