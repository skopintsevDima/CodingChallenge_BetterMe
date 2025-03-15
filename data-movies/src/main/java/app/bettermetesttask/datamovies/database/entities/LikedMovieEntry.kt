package app.bettermetesttask.datamovies.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_movies")
data class LikedMovieEntry(
    @PrimaryKey @ColumnInfo(name = "movie_id")
    val movieId: Int
)