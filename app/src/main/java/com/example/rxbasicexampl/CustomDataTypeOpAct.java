package com.example.rxbasicexampl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CustomDataTypeOpAct extends AppCompatActivity {

    private static final String TAG = CustomDataTypeOpAct.class.getSimpleName();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // add to Composite observable
        // .map() operator is used to turn the note into all uppercase letters
        compositeDisposable.add(getNotesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Notes, Notes>() {
                    @Override
                    public Notes apply(Notes notes) throws Exception {
                        // Making the note to all uppercase
                        notes.setNote(notes.getNote().toUpperCase());
                        return notes;
                    }
                })
        .subscribeWith(getNotesObserver()));

    }

    private DisposableObserver<Notes> getNotesObserver() {
        return new DisposableObserver<Notes>() {
            @Override
            public void onNext(Notes notes) {
                Log.d(TAG, "onNext: "+notes.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: "+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: "+"All notes are emitted");
            }
        };
    }

    private Observable<Notes> getNotesObservable() {
        final List<Notes> notes = prepareNotes();
        return Observable.create(new ObservableOnSubscribe<Notes>() {
            @Override
            public void subscribe(ObservableEmitter<Notes> emitter) throws Exception {
                for (Notes note : notes) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(note);
                    }
                }
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Notes> prepareNotes() {
        List<Notes> notes = new ArrayList<>();
        notes.add(new Notes(1, "buy tooth paste!"));
        notes.add(new Notes(2, "call brother!"));
        notes.add(new Notes(3, "watch narcos tonight!"));
        notes.add(new Notes(4, "pay power bill!"));
        return notes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
