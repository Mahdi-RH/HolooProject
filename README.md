# HolooProject

Using:

.Kotlin
.MVVM architectural pattern
.clean Code
.Hilt library
.Coroutines flow
.Retrofit
.lifecycle
.navigation
.gson
.Room ORM

- We have BaseBindingActivity and BaseBindingFragment as base classes for every activity and fragment which is responsible for implementing view-binding.
In this way, if we wanna add something that can be used and available in all of the activities and fragments in the future, it can be easy to handle. 
