状态模式



### 实例

[来源](https://github.com/apache/tomcat/blob/trunk/java/org/apache/catalina/util/LifecycleBase.java#L128)

```java
public abstract class LifecycleBase implements Lifecycle {
    
	@Override
    public final synchronized void init() throws LifecycleException {
        if (!state.equals(LifecycleState.NEW)) {
            invalidTransition(Lifecycle.BEFORE_INIT_EVENT);
        }

        try {
            setStateInternal(LifecycleState.INITIALIZING, null, false);
            initInternal();
            setStateInternal(LifecycleState.INITIALIZED, null, false);
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.initFail", toString());
        }
    }


    /**
     * Sub-classes implement this method to perform any instance initialisation
     * required.
     *
     * @throws LifecycleException If the initialisation fails
     */
    protected abstract void initInternal() throws LifecycleException;


    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void start() throws LifecycleException {

        if (LifecycleState.STARTING_PREP.equals(state) || LifecycleState.STARTING.equals(state) ||
                LifecycleState.STARTED.equals(state)) {

            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyStarted", toString()), e);
            } else if (log.isInfoEnabled()) {
                log.info(sm.getString("lifecycleBase.alreadyStarted", toString()));
            }

            return;
        }

        if (state.equals(LifecycleState.NEW)) {
            init();
        } else if (state.equals(LifecycleState.FAILED)) {
            stop();
        } else if (!state.equals(LifecycleState.INITIALIZED) &&
                !state.equals(LifecycleState.STOPPED)) {
            invalidTransition(Lifecycle.BEFORE_START_EVENT);
        }

        try {
            setStateInternal(LifecycleState.STARTING_PREP, null, false);
            startInternal();
            if (state.equals(LifecycleState.FAILED)) {
                // This is a 'controlled' failure. The component put itself into the
                // FAILED state so call stop() to complete the clean-up.
                stop();
            } else if (!state.equals(LifecycleState.STARTING)) {
                // Shouldn't be necessary but acts as a check that sub-classes are
                // doing what they are supposed to.
                invalidTransition(Lifecycle.AFTER_START_EVENT);
            } else {
                setStateInternal(LifecycleState.STARTED, null, false);
            }
        } catch (Throwable t) {
            // This is an 'uncontrolled' failure so put the component into the
            // FAILED state and throw an exception.
            handleSubClassException(t, "lifecycleBase.startFail", toString());
        }
    }


    /**
     * Sub-classes must ensure that the state is changed to
     * {@link LifecycleState#STARTING} during the execution of this method.
     * Changing state will trigger the {@link Lifecycle#START_EVENT} event.
     *
     * If a component fails to start it may either throw a
     * {@link LifecycleException} which will cause it's parent to fail to start
     * or it can place itself in the error state in which case {@link #stop()}
     * will be called on the failed component but the parent component will
     * continue to start normally.
     *
     * @throws LifecycleException Start error occurred
     */
    protected abstract void startInternal() throws LifecycleException;


    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void stop() throws LifecycleException {

        if (LifecycleState.STOPPING_PREP.equals(state) || LifecycleState.STOPPING.equals(state) ||
                LifecycleState.STOPPED.equals(state)) {

            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyStopped", toString()), e);
            } else if (log.isInfoEnabled()) {
                log.info(sm.getString("lifecycleBase.alreadyStopped", toString()));
            }

            return;
        }

        if (state.equals(LifecycleState.NEW)) {
            state = LifecycleState.STOPPED;
            return;
        }

        if (!state.equals(LifecycleState.STARTED) && !state.equals(LifecycleState.FAILED)) {
            invalidTransition(Lifecycle.BEFORE_STOP_EVENT);
        }

        try {
            if (state.equals(LifecycleState.FAILED)) {
                // Don't transition to STOPPING_PREP as that would briefly mark the
                // component as available but do ensure the BEFORE_STOP_EVENT is
                // fired
                fireLifecycleEvent(BEFORE_STOP_EVENT, null);
            } else {
                setStateInternal(LifecycleState.STOPPING_PREP, null, false);
            }

            stopInternal();

            // Shouldn't be necessary but acts as a check that sub-classes are
            // doing what they are supposed to.
            if (!state.equals(LifecycleState.STOPPING) && !state.equals(LifecycleState.FAILED)) {
                invalidTransition(Lifecycle.AFTER_STOP_EVENT);
            }

            setStateInternal(LifecycleState.STOPPED, null, false);
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.stopFail", toString());
        } finally {
            if (this instanceof Lifecycle.SingleUse) {
                // Complete stop process first
                setStateInternal(LifecycleState.STOPPED, null, false);
                destroy();
            }
        }
    }


    /**
     * Sub-classes must ensure that the state is changed to
     * {@link LifecycleState#STOPPING} during the execution of this method.
     * Changing state will trigger the {@link Lifecycle#STOP_EVENT} event.
     *
     * @throws LifecycleException Stop error occurred
     */
    protected abstract void stopInternal() throws LifecycleException;


    @Override
    public final synchronized void destroy() throws LifecycleException {
        if (LifecycleState.FAILED.equals(state)) {
            try {
                // Triggers clean-up
                stop();
            } catch (LifecycleException e) {
                // Just log. Still want to destroy.
                log.warn(sm.getString(
                        "lifecycleBase.destroyStopFail", toString()), e);
            }
        }

        if (LifecycleState.DESTROYING.equals(state) || LifecycleState.DESTROYED.equals(state)) {
            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyDestroyed", toString()), e);
            } else if (log.isInfoEnabled() && !(this instanceof Lifecycle.SingleUse)) {
                // Rather than have every component that might need to call
                // destroy() check for SingleUse, don't log an info message if
                // multiple calls are made to destroy()
                log.info(sm.getString("lifecycleBase.alreadyDestroyed", toString()));
            }

            return;
        }

        if (!state.equals(LifecycleState.STOPPED) && !state.equals(LifecycleState.FAILED) &&
                !state.equals(LifecycleState.NEW) && !state.equals(LifecycleState.INITIALIZED)) {
            invalidTransition(Lifecycle.BEFORE_DESTROY_EVENT);
        }

        try {
            setStateInternal(LifecycleState.DESTROYING, null, false);
            destroyInternal();
            setStateInternal(LifecycleState.DESTROYED, null, false);
        } catch (Throwable t) {
            handleSubClassException(t, "lifecycleBase.destroyFail", toString());
        }
    }


    /**
     * Sub-classes implement this method to perform any instance destruction
     * required.
     *
     * @throws LifecycleException If the destruction fails
     */
    protected abstract void destroyInternal() throws LifecycleException;
    
}
```



典型的状态模式