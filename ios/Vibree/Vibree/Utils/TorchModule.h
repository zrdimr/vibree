#import <Foundation/Foundation.h>

@interface TorchModule : NSObject

- (nullable instancetype)initWithFileAtPath:(NSString *)filePath;
- (nullable NSArray<NSNumber *> *)predictPayload:(NSArray<NSNumber *> *)payload;
- (nullable NSArray<NSNumber *> *)predictNLPWithIds:(NSArray<NSNumber *> *)inputIds 
                                              mask:(NSArray<NSNumber *> *)mask;

@end
