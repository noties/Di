package ru.noties.di.android;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.di.Di;
import ru.noties.di.Visitor;

public abstract class ViewInjector {

    @NonNull
    public static Visitor<Di> init(@NonNull final ViewGroup parent) {
        return new Visitor<Di>() {
            @Override
            public void visit(@NonNull final Di di) {

                final ViewGroup.OnHierarchyChangeListener listener = new ViewGroup.OnHierarchyChangeListener() {
                    @Override
                    public void onChildViewAdded(View parent, View child) {

                        if (child instanceof Di.Service) {
                            ((Di.Service) child).init(di);
                        }

                        if (child instanceof ViewGroup) {
                            ((ViewGroup) child).setOnHierarchyChangeListener(this);
                        }
                    }

                    @Override
                    public void onChildViewRemoved(View parent, View child) {
                        // maybe there is no need for this
//                        if (child instanceof ViewGroup) {
//                            ((ViewGroup) child).setOnHierarchyChangeListener(null);
//                        }
                    }
                };

                parent.setOnHierarchyChangeListener(listener);

                applyToChildren(di, parent, listener);
            }

            private void applyToChildren(
                    @NonNull Di di,
                    @NonNull ViewGroup parent,
                    @NonNull ViewGroup.OnHierarchyChangeListener listener) {
                final int count = parent.getChildCount();
                if (count > 0) {
                    View view;
                    for (int i = 0; i < count; i++) {

                        view = parent.getChildAt(i);

                        if (view instanceof Di.Service) {
                            ((Di.Service) view).init(di);
                        }

                        if (view instanceof ViewGroup) {
                            final ViewGroup group = (ViewGroup) view;
                            group.setOnHierarchyChangeListener(listener);
                            applyToChildren(di, group, listener);
                        }
                    }
                }
            }
        };
    }

    private ViewInjector() {
    }
}
