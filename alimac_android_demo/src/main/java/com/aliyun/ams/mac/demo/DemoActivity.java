package com.aliyun.ams.mac.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.aliyun.ams.mac.api.Call;
import com.aliyun.ams.mac.api.Callback;
import com.aliyun.ams.mac.api.MacClient;
import com.aliyun.ams.mac.api.MacException;
import com.aliyun.ams.mac.api.Request;
import com.aliyun.ams.mac.api.Response;

public class DemoActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "DemoActivity";

    private static final String DA_NORMAL_URL = "https://xiuwu.cdnte.com/test.html";
    private static final String SA_NORMAL_URL = "https://xiuwu.cdnte.com/test-static.html";

    private static final String DA_GENERIC_URL = "https://abc.lianke.wildcard.com/test.html";
    private static final String SA_GENERIC_URL = "https://abc.lianke.wildcard.com/test-static.html";

    private static final String DEGRADE_URL = "https://aliyun.com";

    TextView editText;

    MacClient client = new MacClient.Builder().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        editText = (TextView)findViewById(R.id.tvConsoleText);

        findViewById(R.id.btn_normal_dyn).setOnClickListener(this);
        findViewById(R.id.btn_normal_static).setOnClickListener(this);
        findViewById(R.id.btn_generic_dyn).setOnClickListener(this);
        findViewById(R.id.btn_generic_static).setOnClickListener(this);
        findViewById(R.id.btn_degradable).setOnClickListener(this);
        findViewById(R.id.btn_press_dyn).setOnClickListener(this);
        findViewById(R.id.btn_press_static).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        editText.setText("");

        if (R.id.btn_normal_dyn == id) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    String body = "post body";

                    final Request req = new Request.Builder()
                            .url(DA_NORMAL_URL)
                            .addHeader("User-Agent", "demo")
                            .method("POST", body.getBytes())
                            .build();

                    Response rsp = null;
                    try {
                        rsp = client.newCall(req).execute();
                    } catch (final MacException e) {
                        e.printStackTrace();

                        editText.post(new Runnable() {
                            @Override
                            public void run() {
                                editText.setText(e.getMessage());
                            }
                        });
                    }

                    if (rsp != null) {
                        int statusCode = rsp.code();
                        byte[] data = rsp.body();
                        final String s = data != null ? new String(data) : "";
                        Log.d(TAG, "[DemoActivity] execute statusCode: " + statusCode + " data: " + s);

                        editText.post(new Runnable() {
                            @Override
                            public void run() {
                                editText.setText(s);
                            }
                        });
                    }
                }
            }).start();
        } else if (R.id.btn_normal_static == id) {
            final Request req = new Request.Builder()
                    .url(SA_NORMAL_URL)
                    .addHeader("User-Agent", "demo")
                    .build();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.newCall(req).enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            int statusCode = response.code();
                            byte[] data = response.body();
                            final String s = new String(data);
                            Log.d(TAG, "[DemoActivity] execute statusCode: " + statusCode + " data: " + s);

                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.setText(s);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call call, final MacException e) {
                            Log.d(TAG, e.getMessage(), e);

                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.setText(e.getMessage());
                                }
                            });
                        }
                    });
                }
            }).start();
        } else if (R.id.btn_generic_dyn == id) {
            final Request req = new Request.Builder()
                    .url(DA_GENERIC_URL)
                    .addHeader("User-Agent", "demo")
                    .build();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.newCall(req).enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            int statusCode = response.code();
                            byte[] data = response.body();
                            final String s = new String(data);
                            Log.d(TAG, "[DemoActivity] execute statusCode: " + statusCode + " data: " + s);

                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.setText(s);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call call, final MacException e) {
                            Log.d(TAG, e.getMessage(), e);

                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.setText(e.getMessage());
                                }
                            });
                        }
                    });
                }
            }).start();
        } else if (R.id.btn_generic_static == id) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Request req = new Request.Builder()
                            .url(SA_GENERIC_URL)
                            .addHeader("User-Agent", "demo")
                            .build();

                    Response rsp = null;
                    try {
                        rsp = client.newCall(req).execute();
                    } catch (final MacException e) {
                        e.printStackTrace();

                        editText.post(new Runnable() {
                            @Override
                            public void run() {
                                editText.setText(e.getMessage());
                            }
                        });
                    }

                    if (rsp != null) {
                        int statusCode = rsp.code();
                        byte[] data = rsp.body();
                        final String s = data != null ? new String(data) : "";
                        Log.d(TAG, "[DemoActivity] execute statusCode: " + statusCode + " data: " + s);

                        editText.post(new Runnable() {
                            @Override
                            public void run() {
                                editText.setText(s);
                            }
                        });
                    }
                }
            }).start();
        } else if (R.id.btn_press_dyn == id) {
            for (int i = 0; i < 1000; i++) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String body = "post body";

                        final Request req = new Request.Builder()
                                .url(DA_NORMAL_URL)
                                .addHeader("User-Agent", "demo")
                                .method("POST", body.getBytes())
                                .build();

                        Response rsp = null;
                        try {
                            rsp = client.newCall(req).execute();
                        } catch (final MacException e) {
                            e.printStackTrace();

                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.setText(e.getMessage());
                                }
                            });
                        }

                        if (rsp != null) {
                            int statusCode = rsp.code();
                            byte[] data = rsp.body();
                            final String s = data != null ? new String(data) : "";
                            Log.d(TAG, "[DemoActivity] execute statusCode: " + statusCode + " data: " + s);

                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.setText(s);
                                }
                            });
                        }
                    }
                }).start();
            }
        } else if (R.id.btn_press_static == id) {
            for (int i = 0; i < 1000; i++) {
                final Request req = new Request.Builder()
                        .url(SA_NORMAL_URL)
                        .addHeader("User-Agent", "demo")
                        .build();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.newCall(req).enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                int statusCode = response.code();
                                byte[] data = response.body();
                                final String s = new String(data);
                                Log.d(TAG, "[DemoActivity] execute statusCode: " + statusCode + " data: " + s);

                                editText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        editText.setText(s);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call call, final MacException e) {
                                Log.d(TAG, e.getMessage(), e);

                                editText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        editText.setText(e.getMessage());
                                    }
                                });
                            }
                        });
                    }
                }).start();
            }
        } else {
            final Request req = new Request.Builder()
                    .url(DEGRADE_URL)
                    .addHeader("User-Agent", "demo")
                    .build();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.newCall(req).enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            int statusCode = response.code();
                            byte[] data = response.body();
                            final String s = new String(data);
                            Log.d(TAG, "[DemoActivity] execute statusCode: " + statusCode + " data: " + s);

                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.setText(s);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call call, final MacException e) {
                            Log.d(TAG, e.getMessage(), e);


                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.setText(e.getMessage());
                                }
                            });
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about_us:
                AboutActivity.actionStart(this.getApplicationContext());
                return true;
            case R.id.action_helper:
                HelperActivity.actionStart(this.getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
