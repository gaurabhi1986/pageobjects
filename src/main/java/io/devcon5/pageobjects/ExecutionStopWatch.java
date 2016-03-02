/*
 * Copyright 2015-2016 DevCon5 GmbH, info@devcon5.ch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.devcon5.pageobjects;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A functional stop watch for measuring the time for executing a specific task
 */
public class ExecutionStopWatch {

    private final Runnable runnable;
    private final AtomicReference<Object> result = new AtomicReference<>(Void.TYPE);

    private Duration duration;

    private ExecutionStopWatch(Runnable r) {
        this.runnable = () -> r.run();
    }

    private ExecutionStopWatch(Callable c) {
        this.runnable = () -> {
            try {
                result.set(c.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Creates a new stop watch for measuring the execution time of a runnable.
     * @param r
     *  the runnable whose execution time should be measured
     * @return
     *  a new stop watch
     */
    public static ExecutionStopWatch measure(Runnable r){
        return new ExecutionStopWatch(r).run();
    }

    /**
     * Creates a new stop watch for measuring the execution time of a callable
     * @param c
     *  the callable whose execution time should be measured
     * @return
     *  a new stop watch
     */
    public static ExecutionStopWatch measure(Callable c){
        return new ExecutionStopWatch(c).run();
    }

    /**
     * Performs the measured operation
     * @return
     *  this stopwatch
     */
    private ExecutionStopWatch run() {
        final long start = System.nanoTime();
        try {
            this.runnable.run();
        } finally {
            this.duration = Duration.ofNanos(System.nanoTime() - start);
        }
        return this;
    }

    /**
     *
     * @return
     *  the duration of the execution
     */
    public Duration getDuration(){
        return this.duration;
    }

    /**
     *
     * @return
     *  the result of the operation. In case the operation was a runnable, the result is always {@link java.lang.Void}
     *  while the result of a callable is optional.
     */
    public Optional<Object> getResult(){
        return Optional.ofNullable(this.result.get());
    }
}
