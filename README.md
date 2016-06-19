# Popular Movies

This a project developed as a part of Udacity's Android Developer Nanodegree program, owing to the Tata Trusts and Google India Scholarship Program. It fetches a list of movies sorted according to the user's preference. The app stores data locally on the device using a Content Provider and displays the same to the user using a Cursor Loader.

*Popular Movies* was evaluated by the Udacity code reviewer as "Meets Specifications".

## Features

* View the list of the 'Most Popular' movies or the 'Highest Rated' ones, alongwith revelant details of each film.
* Mark movies as favorite to be stored locally to enable viewing even when the user is offline
* Watch trailers and Read reviews 

## Getting Started

The app fetches movie data using [The Movie Database](https://www.themoviedb.org/documentation/api) API. You need to have your own API Key for running the app. When you get it, replace YOUR_API_KEY with the key you received in the following file:
    ```
    app/src/main/res/values/strings.xml
    ```

## Screenshots

![screen](../master/screens/Phone/1.png)

![screen](../master/screens/Phone/2.png)

![screen](../master/screens/Phone/3.png)

![screen](../master/screens/Tablets/1.png)

## Libraries

* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Picasso](http://square.github.io/picasso/)
* [Android Volley](https://github.com/mcxiaoke/android-volley)
* [Android Support Library](https://developer.android.com/topic/libraries/support-library/index.html)
* [Android Design Library](http://android-developers.blogspot.in/2015/05/android-design-support-library.html)
* [Recycler View](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html) & [Card View](https://developer.android.com/reference/android/support/v7/widget/CardView.html)

## Android Developer Nanodegree
[![udacity][1]][2]

[1]: ../master/screens/nanodegree.png
[2]: https://www.udacity.com/course/android-developer-nanodegree--nd801

## License

    Copyright 2016 Aman Dalmia

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
