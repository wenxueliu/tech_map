@startuml


abstract class Abstraction
class RefinedAbstraction
interface Implementor
class ConcreteImplementor1

Abstraction o-- Implementor

abstract class Abstraction {
    - Implementor imp
    + Abstraction(Implementor imp)
    + void request()
    + Implemntor getImp()
}

class RefinedAbstraction extends Abstraction {
    + RefinedAbstraction(Implementor imp)
    + void request()
}

interface Implementor {
    + viud doSomething()
    + void doAnything()
}

class ConcreteImplementor1 implements Implementor {
    + viud doSomething()
    + void doAnything()
}

class ConcreteImplementor2 implements Implementor {
    + viud doSomething()
    + void doAnything()
}

@enduml
