import Foundation

class BertTokenizer {
    private var vocab: [String: Int] = [:]
    
    init() {
        if let path = Bundle.main.path(forResource: "vocab", ofType: "txt") {
            do {
                let content = try String(contentsOfFile: path)
                let lines = content.components(separatedBy: .newlines)
                for (index, line) in lines.enumerated() {
                    let word = line.trimmingCharacters(in: .whitespaces)
                    if !word.isEmpty {
                        vocab[word] = index
                    }
                }
            } catch {
                print("Error loading vocab: \(error)")
            }
        }
    }
    
    func tokenize(_ text: String, maxLength: Int = 128) -> (inputIds: [Int64], attentionMask: [Int64]) {
        var tokens: [Int64] = []
        var inputIds = [Int64](repeating: 0, count: maxLength)
        var attentionMask = [Int64](repeating: 0, count: maxLength)
        
        tokens.append(Int64(vocab["[CLS]"] ?? 101))
        
        let words = text.lowercased().components(separatedBy: CharacterSet.whitespacesAndNewlines)
        for word in words {
            if tokens.count >= maxLength - 1 { break }
            if let id = vocab[word] {
                tokens.append(Int64(id))
            } else {
                tokens.append(Int64(vocab["[UNK]"] ?? 100))
            }
        }
        
        if tokens.count < maxLength {
            tokens.append(Int64(vocab["[SEP]"] ?? 102))
        }
        
        for i in 0..<tokens.count {
            if i < maxLength {
                inputIds[i] = tokens[i]
                attentionMask[i] = 1
            }
        }
        
        return (inputIds, attentionMask)
    }
}
