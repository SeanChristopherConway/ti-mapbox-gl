# $1 = module project name, eg. matise.mapbox
# $2 = app name, eg. maptests
# Place this script in your module folder
#Example usage sh build_droid.sh matise.mapbox maptests
cd android;ant;unzip  dist/$1*.zip -d tmp;rm -rf "/Users/Sean/Documents/Appcelerator_Studio_Workspace/$2/modules/android/$1" ;cp  -R tmp/modules/android/$1 /Users/Sean/Documents/Appcelerator_Studio_Workspace/$2/modules/android;rm -rf tmp;
