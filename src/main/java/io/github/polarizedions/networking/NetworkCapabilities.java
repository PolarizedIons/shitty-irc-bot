package io.github.polarizedions.networking;

import java.util.ArrayList;

/**
 * Copyright 2017 PolarizedIons
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
public class NetworkCapabilities {
    private ArrayList<String> requestedCaps;
    private ArrayList<String> supportedCaps;
    private ArrayList<String> activatedCaps;
    private boolean locked = false;

    public NetworkCapabilities() {
        requestedCaps = new ArrayList<>();
        supportedCaps = new ArrayList<>();
        activatedCaps = new ArrayList<>();
    }

    public void requestCap(String cap) {
        if (locked) {
            return;
        }

        requestedCaps.add(cap);
    }

    protected void addSupportedCap(String cap) {
        if (locked) {
            return;
        }

        supportedCaps.add(cap);
    }

    protected void addActivatedCap(String cap) {
        if (locked) {
            return;
        }

        activatedCaps.add(cap);
    }

    protected void lock() {
        locked = true;
    }

    public ArrayList<String> getRequestedCaps() {
        return requestedCaps;
    }

    public ArrayList<String> getSupportedCaps() {
        return supportedCaps;
    }

    public ArrayList<String> getActivatedCaps() {
        return activatedCaps;
    }

    public boolean isSupported(String cap) {
        if (!locked) {
            return false;
        }

        return activatedCaps.contains(cap);
    }
}
