package com.example.tfs.util

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.retryWhenError(retryCount: Int, delayInSeconds: Long): Observable<T> {
    return retryWhen { errors ->
        errors.zipWith(
            Observable.range(1, retryCount),
            { throwable: Throwable, count: Int -> Pair(throwable, count) })
            .flatMap { count: Pair<Throwable, Int> ->
                if (count.second < retryCount) {
                    Observable.timer(delayInSeconds, TimeUnit.SECONDS)
                } else {
                    Observable.error(count.first)
                }
            }
    }
}