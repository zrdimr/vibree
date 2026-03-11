#import "TorchModule.h"
#import <LibTorch-Lite/LibTorch-Lite.h>

@implementation TorchModule {
 @protected
  torch::jit::mobile::Module _impl;
}

- (nullable instancetype)initWithFileAtPath:(NSString *)filePath {
  self = [super init];
  if (self) {
    try {
      _impl = torch::jit::_load_for_mobile(filePath.UTF8String);
    } catch (const std::exception& exception) {
      NSLog(@"%s", exception.what());
      return nil;
    }
  }
  return self;
}

- (nullable NSArray<NSNumber *> *)predictPayload:(NSArray<NSNumber *> *)payload {
  try {
    at::Tensor tensor = torch::ones({1, 1, 34}, at::kFloat);
    float* data = tensor.data_ptr<float>();
    for (int i = 0; i < MIN(payload.count, 34); i++) {
      data[i] = [payload[i] floatValue];
    }
    
    auto outputTensor = _impl.forward({tensor}).toTensor();
    float* outputData = outputTensor.data_ptr<float>();
    NSMutableArray* result = [NSMutableArray arrayWithCapacity:2];
    for (int i = 0; i < 2; i++) {
      [result addObject:@(outputData[i])];
    }
    return result;
  } catch (const std::exception& exception) {
    NSLog(@"%s", exception.what());
    return nil;
  }
}

- (nullable NSArray<NSNumber *> *)predictNLPWithIds:(NSArray<NSNumber *> *)inputIds 
                                              mask:(NSArray<NSNumber *> *)mask {
    try {
        at::Tensor idsTensor = torch::ones({1, 128}, at::kLong);
        at::Tensor maskTensor = torch::ones({1, 128}, at::kLong);
        
        int64_t* idsData = idsTensor.data_ptr<int64_t>();
        int64_t* maskData = maskTensor.data_ptr<int64_t>();
        
        for (int i = 0; i < MIN(inputIds.count, 128); i++) {
            idsData[i] = (int64_t)[inputIds[i] longLongValue];
        }
        for (int i = 0; i < MIN(mask.count, 128); i++) {
            maskData[i] = (int64_t)[mask[i] longLongValue];
        }
        
        auto outputTensor = _impl.forward({idsTensor, maskTensor}).toTensor();
        float* outputData = outputTensor.data_ptr<float>();
        NSMutableArray* result = [NSMutableArray arrayWithCapacity:2];
        for (int i = 0; i < 2; i++) {
            [result addObject:@(outputData[i])];
        }
        return result;
    } catch (const std::exception& exception) {
        NSLog(@"%s", exception.what());
        return nil;
    }
}

@end
