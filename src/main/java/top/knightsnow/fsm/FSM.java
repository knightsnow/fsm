package top.knightsnow.fsm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public class FSM<E extends Enum<E>> implements ChangeState<E> {

    private E e;

    private final Map<Vector<E>, Action> vectors;

    public FSM(Map<Vector<E>, Action> vectors) {
        this.vectors = vectors;
    }

    public void set(E e) {
        if (this.e == null) {
            this.e = e;
            return;
        }
        Vector<E> vector = new Vector<>(this.e, e);
        if (vectors.containsKey(vector)) {
            this.e = e;
            vectors.get(vector).doIt();
        } else {
            throw new IllegalStateException();
        }
    }


    public static void main(String[] args) {

    }
}

class ProxyInvocationHandler<E extends Enum<E>> implements InvocationHandler, ProxyHandle<E> {

    private final ChangeState<E> changeState;

    public ProxyInvocationHandler(ChangeState<E> changeState) {
        this.changeState = changeState;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object before = before(changeState);
        Object invoke = method.invoke(changeState, args);
        Object after = after(changeState, before);
        return invoke;
    }

    @Override
    public Object before(ChangeState<E> e) {
        return null;
    }

    @Override
    public Object after(ChangeState<E> e, Object before) {
        return null;
    }
}

interface ProxyHandle<E extends Enum<E>> {

    Object before(ChangeState<E> e);

    Object after(ChangeState<E> e, Object before);
}

class Vector<E extends Enum<?>> {

    private final E from;
    private final E to;
    private int weight = 1;

    public Vector(E from, E to) {
        this.from = from;
        this.to = to;
    }

    public Vector(E from, E to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public E getFrom() {
        return from;
    }

    public E getTo() {
        return to;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;
        Vector<?> vector = (Vector<?>) o;
        return weight == vector.weight && from.equals(vector.from) && to.equals(vector.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }
}


interface ChangeState<E extends Enum<E>> {
    void set(E e);
}

@FunctionalInterface
interface Action {

    void doIt();
}