#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface TorchModule : NSObject

- (nullable instancetype)initWithFileAtPath:(NSString * _Nonnull)filePath;
- (nullable NSArray<NSNumber *> *)predictPayload:(NSArray<NSNumber *> * _Nonnull)payload
    NS_SWIFT_NAME(predictPayload(_:));
- (nullable NSArray<NSNumber *> *)predictNLPWithIds:(NSArray<NSNumber *> * _Nonnull)inputIds 
                                              mask:(NSArray<NSNumber *> * _Nonnull)mask
    NS_SWIFT_NAME(predictNLP(withIds:mask:));

@end

NS_ASSUME_NONNULL_END
