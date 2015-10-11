#### Popular Movies

First Udacity's nanodegree project

##### Part I 

~~I choose to implement the [MovieDB API Call](http://api.themoviedb.org/3/discover/movie?api_key=) as an IntentService~~
Change implementation from intent service to AsyncTask as suggested by reviewer

##### Implementation Steps

1. Basic version of MovieService **[COMPLETED]**
2. Movie class as Model **[COMPLETED]**
3. Implement the JSON parser **[COMPLETED]**
4. Implement DownloadReceiver to notify MainActivity when MovieService return **[COMPLETED]**
5. Built some basic UI, and custom adapter **[COMPLETED]**
6. _Fixed_ GridView spacing **[COMPLETED]**
7. Basic UI for Detail Activity **[COMPLETED]**
8. Improved UI for Detail Activity (landscape mode) **[COMPLETED]**
9. Deleted logging toast

##### Notes for Reviewers
- API key is placed into **strings.xml**
- Sort order (most popular, highest rated) is selected trough settings Activity (Main Activity menu)


#### PART II

The main features to add for this second part, is to support tablet with custom layout, add favorite sort order, displaying in movie Detail
users review and video trailers.

1. Add support for Tablet (Build a Master - Detail Layout) **[COMPLETED]**
2. Build a content provider with Schematics
