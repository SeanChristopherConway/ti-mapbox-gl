# $1 = module project name, eg. matise.mapbox
# $2 = app name, eg. maptests
# Place this script in your module folder
#Example usage sh build_droid.sh matise.mapbox maptests
cd iphone;titanium build --platform ios --build-only;unzip  matise.mapbox-iphone-1.0.0.zip -d tmp;rm -rf "/Users/Sean/Documents/Appcelerator_Studio_Workspace/$2/modules/iphone/$1" ;cp  -R tmp/modules/iphone/$1 /Users/Sean/Documents/Appcelerator_Studio_Workspace/$2/modules/iphone;rm -rf tmp;
