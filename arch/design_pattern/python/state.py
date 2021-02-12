#!/usr/bin/env python
# encoding: utf-8

class Dispatch(object):
    def __init__(self):
        pass

    def set_init(self, state):
        self.state = state

    def run(self):
        self.state.run()


class State(object):
    def __init__(self, dispatch):
        self.dispatch = dispatch

    def done(self, state):
        self.dispatch.state = state

class Ready(State):
    def __init__(self, dispatch):
        super(Ready, self).__init__(dispatch)

    def run(self):
        print("ready run")
        self.done(Start(self.dispatch))

class Start(State):
    def __init__(self, dispatch):
        super(Start, self).__init__(dispatch)

    def run(self):
        print("start run")
        self.done(Run(self.dispatch))


class Run(State):
    def __init__(self, dispatch):
        super(Run, self).__init__(dispatch)
        self._block = False

    def run(self):
        print("run run")
        self.done(Close(self.dispatch))

class Block(State):
    def __init__(self, dispatch):
        super(Block, self).__init__(dispatch)

    def run(self):
        print("block run")
        self.done()

class Close(State):
    def __init__(self, dispatch):
        super(Close, self).__init__(dispatch)

    def run(self):
        print("close run")


# Test our radio out
if __name__ == '__main__':
    dispatch = Dispatch()
    ready = Ready(dispatch)
    run = Run(dispatch)
    start = Start(dispatch)
    close = Close(dispatch)
    dispatch.set_init(ready)

    dispatch.run()
    dispatch.run()
    dispatch.run()
    dispatch.set_init(ready)
    dispatch.run()
    dispatch.run()
    dispatch.run()
    dispatch.run()
    dispatch.run()
    dispatch.run()
    dispatch.run()
    dispatch.run()
