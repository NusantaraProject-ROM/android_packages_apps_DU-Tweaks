/*
 * Copyright (C) 2020 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dirtyunicorns.tweaks.fragments.team;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.settings.R;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends Activity {

    private List<DevInfoAdapter> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_recyclerview);

        initTeam();

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }
    private void initTeam(){
        RecyclerView mRecycleview = findViewById(R.id.listView);

        setTeamMember("Muhammad Fikri", getString(R.string.developer_title)
                + " / " + getString(R.string.maintainer_title), "Genkzsz11", "Genkzsz11", 
                R.drawable.genkzsz11);
        setTeamMember("Rafiester", getString(R.string.developer_title)
                + " / " + getString(R.string.maintainer_title), "Rafiester", "Rafiester", 
                R.drawable.rafiester);
        setTeamMember("Rizky Benggolo", getString(R.string.developer_title)
                + " / " + getString(R.string.contributor_title), "travarilo", "travarilo", 
                R.drawable.travarilo);
        setTeamMember("Andra Ramadan", getString(R.string.contributor_title), "andrraa", "Andrraa",
                R.drawable.andra);
        setTeamMember("Galih Gusti Priatna", getString(R.string.contributor_title), "wulan17", "wulan17",
                R.drawable.wulan17);
        setTeamMember("Mikazuuu07", getString(R.string.contributor_title), "Mikazuuu07", "Mikazuuu07",
                R.drawable.mikazuuu);
        setTeamMember("Muhammad Fauzan", getString(R.string.maintainer_title), "buglessx", "nowulimited",
                R.drawable.mfauzan);
        setTeamMember("Julian Surya", getString(R.string.maintainer_title), "juliansurya", "JulianSurya",
                R.drawable.juliansurya);
        setTeamMember("Aoihara", getString(R.string.maintainer_title), "Aoihara", "Aoihara",
                R.drawable.aoihara);
        setTeamMember("Hatsune", getString(R.string.maintainer_title), "Hatsune71", "hats721",
                R.drawable.hatsune);
        setTeamMember("Icecream", getString(R.string.maintainer_title), "1cecreamm", "iicecreamm",
                R.drawable.icecream);
        setTeamMember("Kry9toN", getString(R.string.maintainer_title), "Kry9toN", "Kry9toN",
                R.drawable.kry9ton);
        setTeamMember("G M L R", getString(R.string.maintainer_title), "GMLR", "warga_sosmed",
                R.drawable.gmlr);
        setTeamMember("Harun Al Rasyid", getString(R.string.maintainer_title), "goodmeow", "aarunalr",
                R.drawable.harun);
        setTeamMember("Arif JeNong", getString(R.string.maintainer_title), "ariffjenong", "Arifjenong46", 
                R.drawable.arifjenong);
        setTeamMember("Fraschze97", getString(R.string.maintainer_title), "AIV-97", "noob_permanently", 
                R.drawable.fraschze97);
        setTeamMember("Frost", getString(R.string.maintainer_title), "frostg-012", "frostg-012", 
                R.drawable.frost);
        setTeamMember("Glassy", getString(R.string.maintainer_title), "MatchaKoi", "whdbfl",
                R.drawable.glassy);
        setTeamMember("Galang Saputra", getString(R.string.maintainer_title), "mgs28-mh", "hand08", 
                R.drawable.hand08);
        setTeamMember("Mikazuuu07", getString(R.string.contributor_title), "Mikazuuu07", "Mikazuuu07", 
                R.drawable.mikazuuu);
        setTeamMember("A. Dzaky", getString(R.string.maintainer_title), "zkyzzz", "zkyzzz",
                R.drawable.zkyzzz);
        setTeamMember("Grindklast", getString(R.string.maintainer_title), "grindklast", "grindklast",
                R.drawable.grindklast);

        ListAdapter mAdapter = new ListAdapter(mList);
        mRecycleview.setAdapter(mAdapter);
        mRecycleview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.notifyDataSetChanged();
    }

    private void setTeamMember(String devName, String devTitle,
                               String githubLink, String telegram, int devImage) {
        DevInfoAdapter adapter;

        adapter = new DevInfoAdapter();
        adapter.setImage(devImage);
        adapter.setDevName(devName);
        adapter.setDevTitle(devTitle);
        adapter.setGithubName(githubLink);
        adapter.setTelegramName(telegram);
        mList.add(adapter);
    }
}
