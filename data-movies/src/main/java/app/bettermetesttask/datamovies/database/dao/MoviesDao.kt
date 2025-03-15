package app.bettermetesttask.datamovies.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.bettermetesttask.datamovies.database.entities.LikedMovieEntry
import app.bettermetesttask.datamovies.database.entities.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesDao{

    @Query("SELECT * FROM movies")
    suspend fun selectMovies(): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun selectMovieById(id: Int): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("SELECT * FROM liked_movies")
    fun selectLikedEntries(): Flow<List<LikedMovieEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedEntry(entry: LikedMovieEntry)

    @Query("DELETE FROM liked_movies WHERE movie_id = :movieId")
    suspend fun removeLikedEntry(movieId: Int)

    @Query("DELETE FROM movies")
    suspend fun deleteMovies()
}