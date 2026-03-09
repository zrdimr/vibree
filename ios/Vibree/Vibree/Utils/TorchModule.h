#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface TorchModule : NSObject

- (nullable instancetype)initWithFileAtPath:(NSString * _Nonnull)filePath;
- (nullable NSArray<NSNumber *> *)predictPayload:(NSArray<NSNumber *> * _Nonnull)payload;
- (nullable NSArray<NSNumber *> *)predictNLPWithIds:(NSArray<NSNumber *> * _Nonnull)inputIds 
                                              mask:(NSArray<NSNumber *> * _Nonnull)mask;

@end

NS_ASSUME_NONNULL_END
