//
//  MatiseMapboxPointAnnotation.h
//  mapbox
//
//  Created by Timan Rebel on 09/05/16.
//
//

#import <Mapbox/Mapbox.h>

@interface MatiseMapboxPointAnnotation : NSObject <MGLAnnotation>

// As a reimplementation of the MGLAnnotation protocol, we have to add mutable coordinate and (sub)title properties ourselves.
@property (nonatomic, assign) CLLocationCoordinate2D coordinate;
@property (nonatomic, copy, nullable) NSString *title;
@property (nonatomic, copy, nullable) NSString *subtitle;

// Custom properties that we will use to customize the annotation's image.
@property (nonatomic, copy, nullable) NSString *image;
@property (nonatomic, copy, nonnull) NSString *reuseIdentifier;

@end
