/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

#import "TiUtils.h"
#import "MatiseMapboxMapView.h"
#import "MatiseMapboxPointAnnotation.h"

@interface MatiseMapboxMapView () <MGLMapViewDelegate, UIGestureRecognizerDelegate>
@end

@implementation MatiseMapboxMapView

-(void)dealloc
{
    RELEASE_TO_NIL(mapView);
    [super dealloc];
}

-(void)configurationSet
{

    // Initialize with or without style
    if([self proxyValueForKey:@"styleUrl"]) {
        NSString *styleUrl = [self proxyValueForKey:@"styleUrl"];

        ENSURE_STRING(styleUrl);

        mapView = [[MGLMapView alloc] initWithFrame:self.frame styleURL:[NSURL URLWithString:styleUrl]];
    }
    else {
        mapView = [[MGLMapView alloc] initWithFrame:self.frame];
    }

    // Set lat/lng and zoom
    if([self proxyValueForKey:@"lat"] && [self proxyValueForKey:@"lng"]) {
        double lat = [TiUtils  doubleValue:[self proxyValueForKey:@"lat"]];
        double lng = [TiUtils  doubleValue:[self proxyValueForKey:@"lng"]];
        double zoom = 0;
        BOOL animated = NO;

        if([self proxyValueForKey:@"zoom"]) {
            zoom = [TiUtils  doubleValue:[self proxyValueForKey:@"zoom"]];
        }


        if([self proxyValueForKey:@"animated"] != nil)
            animated = [TiUtils boolValue:[self proxyValueForKey:@"animated"]];

        // set the map's center coordinates and zoom level
        [mapView setCenterCoordinate:CLLocationCoordinate2DMake(lat, lng)
                           zoomLevel:zoom
                            animated:animated];
    }


    mapView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;

    BOOL displayHeadingCalibration = YES;

    if([self proxyValueForKey:@"displayHeadingCalibration"] != nil)
        displayHeadingCalibration = [TiUtils boolValue:[self proxyValueForKey:@"displayHeadingCalibration"]];

    mapView.displayHeadingCalibration = displayHeadingCalibration;

    BOOL showUser = YES;

    if([self proxyValueForKey:@"showUser"] != nil)
        showUser = [TiUtils boolValue:[self proxyValueForKey:@"showUser"]];

    mapView.showsUserLocation = showUser;

    [self addSubview:mapView];
    mapView.delegate = self;

    // double tapping zooms the map, so ensure that can still happen
    UITapGestureRecognizer *doubleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:nil];
    doubleTap.numberOfTapsRequired = 2;
    [mapView addGestureRecognizer:doubleTap];

    // delay single tap recognition until it is clearly not a double
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleSingleTap:)];
    [singleTap requireGestureRecognizerToFail:doubleTap];
    singleTap.delegate = self;
    [mapView addGestureRecognizer:singleTap];

    // also, long press for the hell of it
    [mapView addGestureRecognizer:[[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(handleLongPress:)]];


}

- (void)handleSingleTap:(UITapGestureRecognizer *)tap
{
    // convert tap location (CGPoint)
    // to geographic coordinates (CLLocationCoordinate2D)
    CLLocationCoordinate2D location = [mapView convertPoint:[tap locationInView:mapView]
                                       toCoordinateFromView:mapView];

    //NSLog(@"You tapped at: %.5f, %.5f", location.latitude, location.longitude);

    NSDictionary *event = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSString stringWithFormat:@"%f",location.longitude],@"lng",
                           [NSString stringWithFormat:@"%f",location.latitude],@"lat",
                           nil
                           ];

    if ([self.proxy _hasListeners:@"singleTapOnMap"]) {
        [self.proxy fireEvent:@"singleTapOnMap" withObject:event];
    }


}


- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer
{
    // you could check for specific gestures here, but ¯\_(ツ)_/¯
    return YES;
}

- (void)handleLongPress:(UILongPressGestureRecognizer *)longPress
{
    // drop a marker annotation
    if (longPress.state == UIGestureRecognizerStateBegan)
    {
        MGLPointAnnotation *point = [MGLPointAnnotation new];
        point.coordinate = [mapView convertPoint:[longPress locationInView:longPress.view]
                            toCoordinateFromView:mapView];

        NSDictionary *event = [NSDictionary dictionaryWithObjectsAndKeys:
                               [NSString stringWithFormat:@"%f",point.coordinate.longitude],@"lng",
                               [NSString stringWithFormat:@"%f",point.coordinate.latitude],@"lat",
                               nil
                               ];

        if ([self.proxy _hasListeners:@"longPressOnMap"]) {
            [self.proxy fireEvent:@"longPressOnMap" withObject:event];
        }

    }
}

-(void)frameSizeChanged:(CGRect)frame bounds:(CGRect)bounds
{
    if (mapView != nil)
    {
        [TiUtils setView:mapView positionRect:bounds];
    }

    [super frameSizeChanged:frame bounds:bounds];
}

#pragma mark Custom functions
-(void)setCenterCoordinate:(id)args
{
    ENSURE_SINGLE_ARG(args, NSDictionary);

    double lat = [TiUtils doubleValue:[args objectForKey:@"lat"]];
    double lng = [TiUtils doubleValue:[args objectForKey:@"lng"]];
    double zoom = mapView.zoomLevel;
    BOOL animated = YES;

    if([args objectForKey:@"zoom"] != nil)
        zoom = [TiUtils doubleValue:[args objectForKey:@"zoom"]];
    if([args objectForKey:@"animated"] != nil)
        animated = [TiUtils boolValue:[args objectForKey:@"animated"]];

    [mapView setCenterCoordinate:CLLocationCoordinate2DMake(lat, lng)
                       zoomLevel:zoom
                        animated:animated];
}

-(void)setStyleUrl : (id)url
{
    ENSURE_STRING(url);

    @try {

        [mapView setStyleURL : [NSURL URLWithString:url]];
        NSLog(@"Loaded: %@", url);
    } @catch(NSException *theException) {
        NSLog(@"An exception occurred: %@", theException.name);
        NSLog(@"Here are some details: %@", theException.reason);
    } @finally {
        NSLog(@"Executing finally block");

        if ([self.proxy _hasListeners:@"mapReady"]) {
            [self.proxy fireEvent:@"mapReady" withObject:nil];
        }
    }


}

-(void)addAnnotation:(id)args
{
    MatiseMapboxPointAnnotationProxy *annotation = [args objectAtIndex:0];

    [mapView addAnnotation:annotation.marker];

}

-(void)removeAnnotation:(id)args
{
    MatiseMapboxPointAnnotationProxy *annotation = [args objectAtIndex:0];

    [mapView removeAnnotation:annotation.marker];
}

- (void)mapView:(MGLMapView  *)mapView didSelectAnnotation:(id<MGLAnnotation>)annotation
{
    MatiseMapboxPointAnnotation *marker = (MatiseMapboxPointAnnotation *)annotation;
    //NSLog(@"Annotation clicked is %@",marker.subtitle);
    NSDictionary *event = [NSDictionary dictionaryWithObjectsAndKeys:
                           [NSNumber numberWithFloat:marker.coordinate.longitude],@"lng",
                           [NSNumber numberWithFloat:marker.coordinate.latitude],@"lat",
                           marker.subtitle,@"site_info",
                           nil
                           ];

    if ([self.proxy _hasListeners:@"tapOnAnnotation"]) {
        [self.proxy fireEvent:@"tapOnAnnotation" withObject:event];
    }
}


-(void)addGeoJsonSource:(id)args
{
    ENSURE_SINGLE_ARG(args, NSDictionary);
    
    NSString *querySourceID = [TiUtils stringValue:[args objectForKey:@"layer"]];
    NSString *jsonPath = [TiUtils stringValue:[args objectForKey:@"geojson"]];
    
    // Load and serialize the GeoJSON into a dictionary filled with properly-typed objects
    NSDictionary *jsonDict = [NSJSONSerialization JSONObjectWithData:[jsonPath dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    
    MGLSource *source = [mapView.style sourceWithIdentifier:querySourceID];
    if (source) {
        [mapView.style removeSource:source];
    }
    
    // Load the `features` dictionary for iteration
    MGLShapeSource *shpsource = [[MGLShapeSource alloc] initWithIdentifier:querySourceID features:jsonDict[@"features"] options:nil];
    [mapView.style addSource:shpsource];
}



-(void)setUserTrackingMode:(id)args
{
    MatiseMapboxPointAnnotationProxy *annotation = [args objectAtIndex:0];

    [mapView setUserTrackingMode:annotation.marker];
}

#pragma mark Delegate functions
- (CGFloat)mapView:(MGLMapView *)mapView alphaForShapeAnnotation:(MGLShape *)annotation
{
    // Set the alpha for all shape annotations to 1 (full opacity)
    return 1.0f;
}

- (CGFloat)mapView:(MGLMapView *)mapView lineWidthForPolylineAnnotation:(MGLPolyline *)annotation
{
    // Set the line width for polyline annotations
    return 3.0f;
}

- (UIColor *)mapView:(MGLMapView *)mapView strokeColorForShapeAnnotation:(MGLShape *)annotation
{
    return [UIColor colorWithRed:0.94 green:0.79 blue:0.12 alpha:1.0];
}

- (BOOL)mapView:(MGLMapView *)mapView annotationCanShowCallout:(id<MGLAnnotation>)annotation
{
    // return true;
    return false;
}



- (UIView *)mapView:(MGLMapView *)mapView leftCalloutAccessoryViewForAnnotation:(id<MGLAnnotation>)annotation
{
    if ([annotation.title isEqualToString:@"Kinkaku-ji"])
    {
        // callout height is fixed; width expands to fit
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 60.f, 50.f)];
        label.textAlignment = NSTextAlignmentRight;
        label.textColor = [UIColor colorWithRed:0.81f green:0.71f blue:0.23f alpha:1.f];
        label.text = @"金閣寺";

        return label;
    }

    return nil;
}

- (UIView *)mapView:(MGLMapView *)mapView rightCalloutAccessoryViewForAnnotation:(id<MGLAnnotation>)annotation
{
    return [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
}

- (void)mapView:(MGLMapView *)mapView annotation:(id<MGLAnnotation>)annotation calloutAccessoryControlTapped:(UIControl *)control
{
    // hide the callout view
    [mapView deselectAnnotation:annotation animated:NO];

    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:annotation.title
                                                    message:@"A lovely (if touristy) place."
                                                   delegate:nil
                                          cancelButtonTitle:nil
                                          otherButtonTitles:@"OK", nil];
    [alert show];
}

- (MGLAnnotationImage *)mapView:(MGLMapView *)mapView imageForAnnotation:(id <MGLAnnotation>)annotation
{
    MatiseMapboxPointAnnotation *marker = (MatiseMapboxPointAnnotation *)annotation;

    // Try to reuse the existing ‘pisa’ annotation image, if it exists
    MGLAnnotationImage *annotationImage = [mapView dequeueReusableAnnotationImageWithIdentifier:marker.image];

    NSLog(@"Image is %@",marker.image);

    //check if file exists in default Resources dir
    //NSString *filePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent: marker.image];

    //BOOL fileExists = [[NSFileManager defaultManager] fileExistsAtPath:filePath];
    //NSLog(@"file exists in Resources (default): %i", fileExists);

    //NSLog(@"annotationImage is %@", annotationImage);

    // If the ‘pisa’ annotation image hasn‘t been set yet, initialize it here
    if ( ! annotationImage)
    {
        NSString *filePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:marker.image];

        // Leaning Tower of Pisa by Stefan Spieler from the Noun Project
        UIImage *image = [UIImage imageWithContentsOfFile:filePath];

        // The anchor point of an annotation is currently always the center. To
        // shift the anchor point to the bottom of the annotation, the image
        // asset includes transparent bottom padding equal to the original image
        // height.
        //
        // To make this padding non-interactive, we create another image object
        // with a custom alignment rect that excludes the padding.
        image = [image imageWithAlignmentRectInsets:UIEdgeInsetsMake(0, 0, image.size.height/2, 0)];


        // Initialize the ‘pisa’ annotation image with the UIImage we just loaded
        annotationImage = [MGLAnnotationImage annotationImageWithImage:image reuseIdentifier:marker.image];
        //NSLog(@"annotationImage here is %@", annotationImage);
    }


    return annotationImage;
}


@end
