package com.aliyun.apm.android.demo.internal;

import android.os.SystemClock;

public class Lag {
    private static final long LAG_DURATION_MS = 3000L;
    private static final long SAMPLE_INTERVAL_MS = 300L;
    private double busyLoopAccumulator;

    public void blockByShiftingStack() {
        long start = SystemClock.uptimeMillis();
        long end = start + LAG_DURATION_MS;
        while (SystemClock.uptimeMillis() < end) {
            long elapsed = SystemClock.uptimeMillis() - start;
            long segmentIndex = elapsed / SAMPLE_INTERVAL_MS;
            long segmentEnd = Math.min(end, start + (segmentIndex + 1) * SAMPLE_INTERVAL_MS);
            if (segmentIndex == 0) {
                stackPhaseAlpha(segmentEnd);
            } else if (segmentIndex == 1 || segmentIndex == 2) {
                stackPhaseBeta(segmentEnd);
            } else if (segmentIndex >= 3 && segmentIndex <= 7) {
                stackPhaseGamma(segmentEnd, (int) segmentIndex);
            } else {
                stackPhaseDelta(segmentEnd);
            }
        }
    }

    private void stackPhaseAlpha(long segmentEnd) {
        alphaStep(segmentEnd);
    }

    private void stackPhaseBeta(long segmentEnd) {
        betaStep(segmentEnd);
    }

    private void stackPhaseGamma(long segmentEnd, int segmentIndex) {
        gammaStep(segmentEnd, segmentIndex);
    }

    private void stackPhaseDelta(long segmentEnd) {
        deltaStep(segmentEnd);
    }

    private void alphaStep(long segmentEnd) {
        spinUntil(segmentEnd);
    }

    private void betaStep(long segmentEnd) {
        // Additional layer to make stack distinct.
        betaInner(segmentEnd);
    }

    private void betaInner(long segmentEnd) {
        spinUntil(segmentEnd);
    }

    private void gammaStep(long segmentEnd, int segmentIndex) {
        switch (segmentIndex) {
            case 3:
                gammaInner(segmentEnd);
                break;
            case 4:
                gammaBridge(segmentEnd);
                break;
            case 5:
                gammaWrapper(segmentEnd);
                break;
            case 6:
                gammaWrapperDeep(segmentEnd);
                break;
            default:
                gammaWrapperDeeper(segmentEnd);
                break;
        }
    }

    private void gammaInner(long segmentEnd) {
        gammaCore(segmentEnd);
    }

    private void gammaBridge(long segmentEnd) {
        gammaInner(segmentEnd);
    }

    private void gammaWrapper(long segmentEnd) {
        gammaBridge(segmentEnd);
    }

    private void gammaWrapperDeep(long segmentEnd) {
        gammaWrapper(segmentEnd);
    }

    private void gammaWrapperDeeper(long segmentEnd) {
        gammaWrapperDeep(segmentEnd);
    }

    private void gammaCore(long segmentEnd) {
        spinUntil(segmentEnd);
    }

    private void deltaStep(long segmentEnd) {
        deltaInner(segmentEnd);
    }

    private void deltaInner(long segmentEnd) {
        deltaCore(segmentEnd);
    }

    private void deltaCore(long segmentEnd) {
        spinUntil(segmentEnd);
    }

    private void spinUntil(long segmentEnd) {
        while (SystemClock.uptimeMillis() < segmentEnd) {
            busyLoopAccumulator += Math.sqrt(busyLoopAccumulator + 1.0);
        }
    }
}
