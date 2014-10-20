

package freeh.kitsune.stages.deskmate;

import un.api.character.Chars;
import un.api.collection.Dictionary;
import un.api.collection.HashDictionary;
import un.api.collection.Iterator;
import un.api.physic.skeleton.Joint;
import un.api.physic.skeleton.Skeleton;

/**
 * Rename and transform an XNA skeleton to match a PMX skeleton.
 */
public class XNASkeletonToVMD {
 
    private static final Dictionary MMDtoXNA = new HashDictionary();
    private static final Dictionary XNAtoMMD = new HashDictionary();
    static {
        // JPN:全ての親 / GBR:all parents
        // JPN:下半身 / GBR:lower body/bust
        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
        MMDtoXNA.add(new Chars("下半身"),  new Chars("root hips"));
        
        // JPN:左足 / GBR:left foot
        // JPN:左ひざ / GBR:left knee
        // JPN:左足首 / GBR:left foot head/first
        // JPN:左つま先 / GBR:left toe
        MMDtoXNA.add(new Chars("左足"), new Chars("leg left thigh"));
        MMDtoXNA.add(new Chars("左ひざ"), new Chars("leg left knee"));
        MMDtoXNA.add(new Chars("左足首"), new Chars("leg left ankle"));
        MMDtoXNA.add(new Chars("左つま先"), new Chars("leg left toes"));
        
        // JPN:右足 / GBR:right foot
        // JPN:右ひざ / GBR:right knee
        // JPN:右足首 / GBR:right foot head/first
        // JPN:右つま先 / GBR:right toe
        MMDtoXNA.add(new Chars("右足"), new Chars("leg right thigh"));
        MMDtoXNA.add(new Chars("右ひざ"), new Chars("leg right knee"));
        MMDtoXNA.add(new Chars("右足首"), new Chars("leg right ankle"));
        MMDtoXNA.add(new Chars("右つま先"), new Chars("leg right toes"));
        
        // JPN:上半身 / GBR:upper body/bust
        // JPN:上半身2 / GBR:upper body/bust2        
        MMDtoXNA.add(new Chars("上半身"), new Chars("spine lower"));
        MMDtoXNA.add(new Chars("上半身2"), new Chars("spine upper"));
        
        // JPN:首 / GBR:head/first
        // JPN:頭 / GBR:head
        // JPN:左目 / GBR:left eye
        // JPN:右目 / GBR:right eye
        MMDtoXNA.add(new Chars("首"), new Chars("head neck lower"));
        MMDtoXNA.add(new Chars("頭"), new Chars("head neck upper"));
        MMDtoXNA.add(new Chars("左目"), new Chars("head eyeball left"));
        MMDtoXNA.add(new Chars("右目"), new Chars("head eyeball right"));
        
        // JPN:左肩 / GBR:left shoulder
        // JPN:左腕 / GBR:left wrist
        // JPN:左ひじ / GBR:left elbow
        // JPN:腕アクセ / GBR:wrist access
        MMDtoXNA.add(new Chars("左肩"), new Chars("arm left shoulder 1"));
        MMDtoXNA.add(new Chars("左腕"), new Chars("arm left shoulder 2"));
        MMDtoXNA.add(new Chars("左ひじ"), new Chars("arm left elbow"));
        MMDtoXNA.add(new Chars("腕アクセ"), new Chars("arm left wrist"));
        
        
        
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
//        MMDtoXNA.add(new Chars("全ての親"), new Chars("root ground"));
        
        
        //make the inverse map
        final Iterator keyite = MMDtoXNA.getKeys().createIterator();
        while(keyite.hasNext()){
            final Object key = keyite.next();
            XNAtoMMD.add(MMDtoXNA.getValue(key),key);
        }
        
        
    }
    
    public static void adapt(Skeleton skeleton){
        
        //map names
        Iterator ite = skeleton.getJoints().createIterator();
        while(ite.hasNext()){
            Joint jt = (Joint) ite.next();
            Chars name = jt.getName().toChars();
            Chars pmxName = (Chars) XNAtoMMD.getValue(name);
            if(pmxName!=null){
                jt.setName(pmxName);
            }
        }
        
        //rebuild pmx IK
        //TODO
        
    }
        
    
/* PMX MODEL
JPN:全ての親 / GBR:all parents
├─ JPN:センター / GBR:center
│ ├─ JPN:グルーブ / GBR:groove
│ │ ├─ JPN:上半身 / GBR:upper body/bust
│ │ │ ├─ JPN:上半身2 / GBR:upper body/bust2
│ │ │ │ ├─ JPN:首 / GBR:head/first
│ │ │ │ │ ├─ JPN:頭 / GBR:head
│ │ │ │ │ │ ├─ JPN:左目 / GBR:left eye
│ │ │ │ │ │ │ └─ JPN:左目先 / GBR:left eye destination
│ │ │ │ │ │ ├─ JPN:右目 / GBR:right eye
│ │ │ │ │ │ │ └─ JPN:右目先 / GBR:right eye destination
│ │ │ │ │ │ ├─ JPN:舌１ / GBR:tongue１
│ │ │ │ │ │ │ └─ JPN:舌２ / GBR:tongue２
│ │ │ │ │ │ │   └─ JPN:舌３ / GBR:tongue３
│ │ │ │ │ │ │     └─ JPN:舌３先 / GBR:tongue３ destination
│ │ │ │ │ │ ├─ JPN:両目 / GBR:both eye
│ │ │ │ │ │ │ └─ JPN:両目先 / GBR:both eye destination
│ │ │ │ │ │ ├─ JPN:左前髪１ / GBR:left bangs１
│ │ │ │ │ │ │ ├─ JPN:左前髪２ / GBR:left bangs２
│ │ │ │ │ │ │ │ ├─ JPN:左前髪３ / GBR:left bangs３
│ │ │ │ │ │ │ │ │ ├─ JPN:左前髪３先 / GBR:left bangs３ destination
│ │ │ │ │ │ ├─ JPN:右前髪１ / GBR:right bangs１
│ │ │ │ │ │ │ ├─ JPN:右前髪２ / GBR:right bangs２
│ │ │ │ │ │ │ │ ├─ JPN:右前髪３ / GBR:right bangs３
│ │ │ │ │ │ │ │ │ ├─ JPN:右前髪３先 / GBR:right bangs３ destination
│ │ │ │ │ │ ├─ JPN:左横髪１ / GBR:left side hair１
│ │ │ │ │ │ │ ├─ JPN:左横髪２ / GBR:left side hair２
│ │ │ │ │ │ │ │ ├─ JPN:左横髪３ / GBR:left side hair３
│ │ │ │ │ │ │ │ │ ├─ JPN:左横髪４ / GBR:left side hair４
│ │ │ │ │ │ │ │ │ │ ├─ JPN:左横髪４先 / GBR:left side hair４ destination
│ │ │ │ │ │ ├─ JPN:右横髪１ / GBR:right side hair１
│ │ │ │ │ │ │ ├─ JPN:右横髪２ / GBR:right side hair２
│ │ │ │ │ │ │ │ ├─ JPN:右横髪３ / GBR:right side hair３
│ │ │ │ │ │ │ │ │ ├─ JPN:右横髪４ / GBR:right side hair４
│ │ │ │ │ │ │ │ │ │ ├─ JPN:右横髪４先 / GBR:right side hair４ destination
│ │ │ │ │ │ ├─ JPN:左三つ編み１ / GBR:left braid１
│ │ │ │ │ │ │ ├─ JPN:左三つ編み２ / GBR:left braid２
│ │ │ │ │ │ │ │ ├─ JPN:左三つ編み３ / GBR:left braid３
│ │ │ │ │ │ │ │ │ ├─ JPN:左三つ編み４ / GBR:left braid４
│ │ │ │ │ │ │ │ │ │ ├─ JPN:左三つ編み４先 / GBR:left braid４ destination
│ │ │ │ │ │ ├─ JPN:右三つ編み１ / GBR:right braid１
│ │ │ │ │ │ │ ├─ JPN:右三つ編み２ / GBR:right braid２
│ │ │ │ │ │ │ │ ├─ JPN:右三つ編み３ / GBR:right braid３
│ │ │ │ │ │ │ │ │ ├─ JPN:右三つ編み４ / GBR:right braid４
│ │ │ │ │ │ │ │ │ │ ├─ JPN:右三つ編み４先 / GBR:right braid４ destination
│ │ │ │ │ │ ├─ JPN:左イヤリング / GBR:left earring
│ │ │ │ │ │ │ ├─ JPN:左イヤリング先 / GBR:left earring destination
│ │ │ │ │ │ │ └─ DefaultNode
│ │ │ │ │ │ ├─ JPN:右イヤリング / GBR:right earring
│ │ │ │ │ │ │ ├─ JPN:右イヤリング先 / GBR:right earring destination
│ │ │ │ │ │ ├─ JPN:左SCリボン１ / GBR:leftSC ribbon１
│ │ │ │ │ │ │ ├─ JPN:左SCリボン２ / GBR:leftSC ribbon２
│ │ │ │ │ │ │ │ ├─ JPN:左SCリボン３ / GBR:leftSC ribbon３
│ │ │ │ │ │ │ │ │ ├─ JPN:左SCリボン３先 / GBR:leftSC ribbon３ destination
│ │ │ │ │ │ ├─ JPN:右SCリボン１ / GBR:rightSC ribbon１
│ │ │ │ │ │ │ ├─ JPN:右SCリボン２ / GBR:rightSC ribbon２
│ │ │ │ │ │ │ │ ├─ JPN:右SCリボン３ / GBR:rightSC ribbon３
│ │ │ │ │ │ │ │ │ ├─ JPN:右SCリボン３先 / GBR:rightSC ribbon３ destination
│ │ │ │ │ │ ├─ JPN:SCテール１ / GBR:SC tail１
│ │ │ │ │ │ │ ├─ JPN:SCテール２ / GBR:SC tail２
│ │ │ │ │ │ │ │ ├─ JPN:SCテール３ / GBR:SC tail３
│ │ │ │ │ │ │ │ │ ├─ JPN:SCテール３先 / GBR:SC tail３ destination
│ │ │ │ │ │ ├─ JPN:頭先 / GBR:head destination
│ │ │ │ ├─ JPN:左ブラ首紐 / GBR:left bra head/first string/strap
│ │ │ │ │ ├─ JPN:左ブラ首紐先 / GBR:left bra head/first string/strap destination
│ │ │ │ ├─ JPN:右ブラ首紐 / GBR:right bra head/first string/strap
│ │ │ │ │ ├─ JPN:右ブラ首紐先 / GBR:right bra head/first string/strap destination
│ │ │ │ ├─ JPN:左肩 / GBR:left shoulder
│ │ │ │ │ └─ JPN:左腕 / GBR:left wrist
│ │ │ │ │   ├─ JPN:左腕捩 / GBR:left wrist torsion
│ │ │ │ │   │ ├─ JPN:左ひじ / GBR:left elbow
│ │ │ │ │   │ │ ├─ JPN:左手捩 / GBR:left hand torsion
│ │ │ │ │   │ │ │ ├─ JPN:腕アクセ / GBR:wrist access
│ │ │ │ │   │ │ │ │ ├─ JPN:腕アクセ先 / GBR:wrist access destination
│ │ │ │ │   │ │ │ ├─ JPN:左手首 / GBR:left hand head/first
│ │ │ │ │   │ │ │ │ ├─ JPN:左親指０ / GBR:left thumb０
│ │ │ │ │   │ │ │ │ │ └─ JPN:左親指１ / GBR:left thumb１
│ │ │ │ │   │ │ │ │ │   └─ JPN:左親指２ / GBR:left thumb２
│ │ │ │ │   │ │ │ │ │     └─ JPN:左親指２先 / GBR:left thumb２ destination
│ │ │ │ │   │ │ │ │ ├─ JPN:左人指１ / GBR:left human finger１
│ │ │ │ │   │ │ │ │ │ └─ JPN:左人指２ / GBR:left human finger２
│ │ │ │ │   │ │ │ │ │   └─ JPN:左人指３ / GBR:left human finger３
│ │ │ │ │   │ │ │ │ │     └─ JPN:左人指３先 / GBR:left human finger３ destination
│ │ │ │ │   │ │ │ │ ├─ JPN:左中指１ / GBR:left medium finger１
│ │ │ │ │   │ │ │ │ │ └─ JPN:左中指２ / GBR:left medium finger２
│ │ │ │ │   │ │ │ │ │   └─ JPN:左中指３ / GBR:left medium finger３
│ │ │ │ │   │ │ │ │ │     └─ JPN:左中指３先 / GBR:left medium finger３ destination
│ │ │ │ │   │ │ │ │ ├─ JPN:左薬指１ / GBR:left ring finger１
│ │ │ │ │   │ │ │ │ │ └─ JPN:左薬指２ / GBR:left ring finger２
│ │ │ │ │   │ │ │ │ │   └─ JPN:左薬指３ / GBR:left ring finger３
│ │ │ │ │   │ │ │ │ │     └─ JPN:左薬指３先 / GBR:left ring finger３ destination
│ │ │ │ │   │ │ │ │ ├─ JPN:左小指１ / GBR:left pinkie finger１
│ │ │ │ │   │ │ │ │ │ └─ JPN:左小指２ / GBR:left pinkie finger２
│ │ │ │ │   │ │ │ │ │   └─ JPN:左小指３ / GBR:left pinkie finger３
│ │ │ │ │   │ │ │ │ │     └─ JPN:左小指３先 / GBR:left pinkie finger３ destination
│ │ │ │ │   │ │ │ │ └─ JPN:左手先 / GBR:left hand destination
│ │ │ │ │   │ │ │ └─ JPN:左手捩先 / GBR:left hand torsion destination
│ │ │ │ │   │ │ ├─ JPN:左手捩１ / GBR:left hand torsion１
│ │ │ │ │   │ │ ├─ JPN:左手捩２ / GBR:left hand torsion２
│ │ │ │ │   │ │ ├─ JPN:左手捩３ / GBR:left hand torsion３
│ │ │ │ │   │ └─ JPN:左腕捩先 / GBR:left wrist torsion destination
│ │ │ │ │   ├─ JPN:左腕捩１ / GBR:left wrist torsion１
│ │ │ │ │   ├─ JPN:左腕捩２ / GBR:left wrist torsion２
│ │ │ │ │   ├─ JPN:左腕捩３ / GBR:left wrist torsion３
│ │ │ │ ├─ JPN:右肩 / GBR:right shoulder
│ │ │ │ │ └─ JPN:右腕 / GBR:right wrist
│ │ │ │ │   ├─ JPN:右腕捩 / GBR:right wrist torsion
│ │ │ │ │   │ ├─ JPN:右ひじ / GBR:right elbow
│ │ │ │ │   │ │ ├─ JPN:右手捩 / GBR:right hand torsion
│ │ │ │ │   │ │ │ ├─ JPN:右手首 / GBR:right hand head/first
│ │ │ │ │   │ │ │ │ ├─ JPN:右親指０ / GBR:right thumb０
│ │ │ │ │   │ │ │ │ │ └─ JPN:右親指１ / GBR:right thumb１
│ │ │ │ │   │ │ │ │ │   └─ JPN:右親指２ / GBR:right thumb２
│ │ │ │ │   │ │ │ │ │     └─ JPN:右親指２先 / GBR:right thumb２ destination
│ │ │ │ │   │ │ │ │ ├─ JPN:右人指１ / GBR:right human finger１
│ │ │ │ │   │ │ │ │ │ └─ JPN:右人指２ / GBR:right human finger２
│ │ │ │ │   │ │ │ │ │   └─ JPN:右人指３ / GBR:right human finger３
│ │ │ │ │   │ │ │ │ │     └─ JPN:右人指３先 / GBR:right human finger３ destination
│ │ │ │ │   │ │ │ │ ├─ JPN:右中指１ / GBR:right medium finger１
│ │ │ │ │   │ │ │ │ │ └─ JPN:右中指２ / GBR:right medium finger２
│ │ │ │ │   │ │ │ │ │   └─ JPN:右中指３ / GBR:right medium finger３
│ │ │ │ │   │ │ │ │ │     └─ JPN:右中指３先 / GBR:right medium finger３ destination
│ │ │ │ │   │ │ │ │ ├─ JPN:右薬指１ / GBR:right ring finger１
│ │ │ │ │   │ │ │ │ │ └─ JPN:右薬指２ / GBR:right ring finger２
│ │ │ │ │   │ │ │ │ │   └─ JPN:右薬指３ / GBR:right ring finger３
│ │ │ │ │   │ │ │ │ │     └─ JPN:右薬指３先 / GBR:right ring finger３ destination
│ │ │ │ │   │ │ │ │ ├─ JPN:右小指１ / GBR:right pinkie finger１
│ │ │ │ │   │ │ │ │ │ └─ JPN:右小指２ / GBR:right pinkie finger２
│ │ │ │ │   │ │ │ │ │   └─ JPN:右小指３ / GBR:right pinkie finger３
│ │ │ │ │   │ │ │ │ │     └─ JPN:右小指３先 / GBR:right pinkie finger３ destination
│ │ │ │ │   │ │ │ │ └─ JPN:右手先 / GBR:right hand destination
│ │ │ │ │   │ │ │ └─ JPN:右手捩先 / GBR:right hand torsion destination
│ │ │ │ │   │ │ ├─ JPN:右手捩１ / GBR:right hand torsion１
│ │ │ │ │   │ │ ├─ JPN:右手捩２ / GBR:right hand torsion２
│ │ │ │ │   │ │ ├─ JPN:右手捩３ / GBR:right hand torsion３
│ │ │ │ │   │ └─ JPN:右腕捩先 / GBR:right wrist torsion destination
│ │ │ │ │   ├─ JPN:右腕捩１ / GBR:right wrist torsion１
│ │ │ │ │   ├─ JPN:右腕捩２ / GBR:right wrist torsion２
│ │ │ │ │   ├─ JPN:右腕捩３ / GBR:right wrist torsion３
│ │ │ │ ├─ JPN:左胸上 / GBR:left chest upper
│ │ │ │ │ ├─ JPN:左胸上２ / GBR:left chest upper２
│ │ │ │ │ │ ├─ JPN:左胸上２先 / GBR:left chest upper２ destination
│ │ │ │ ├─ JPN:左胸下 / GBR:left chest lower
│ │ │ │ │ ├─ JPN:左胸下先 / GBR:left chest lower destination
│ │ │ │ ├─ JPN:右胸上 / GBR:right chest upper
│ │ │ │ │ ├─ JPN:右胸上２ / GBR:right chest upper２
│ │ │ │ │ │ ├─ JPN:右胸上２先 / GBR:right chest upper２ destination
│ │ │ │ ├─ JPN:右胸下 / GBR:right chest lower
│ │ │ │ │ ├─ JPN:右胸下先 / GBR:right chest lower destination
│ │ │ │ ├─ JPN:左ブラ背中紐 / GBR:left bra back string/strap
│ │ │ │ │ ├─ JPN:左ブラ背中紐先 / GBR:left bra back string/strap destination
│ │ │ │ ├─ JPN:右ブラ背中紐 / GBR:right bra back string/strap
│ │ │ │ │ ├─ JPN:右ブラ背中紐先 / GBR:right bra back string/strap destination
│ │ │ │ ├─ JPN:上半身2先 / GBR:upper body/bust2 destination
│ │ ├─ JPN:下半身 / GBR:lower body/bust
│ │ │ ├─ JPN:スカートセンター / GBR:skirt centerー
│ │ │ │ ├─ JPN:左ぱんつ紐１ / GBR:left pants string/strap１
│ │ │ │ │ ├─ JPN:左ぱんつ紐１先 / GBR:left pants string/strap１ destination
│ │ │ │ ├─ JPN:左ぱんつ紐２ / GBR:left pants string/strap２
│ │ │ │ │ ├─ JPN:左ぱんつ紐２先 / GBR:left pants string/strap２ destination
│ │ │ │ ├─ JPN:右ぱんつ紐１ / GBR:right pants string/strap１
│ │ │ │ │ ├─ JPN:右ぱんつ紐１先 / GBR:right pants string/strap１ destination
│ │ │ │ ├─ JPN:右ぱんつ紐２ / GBR:right pants string/strap２
│ │ │ │ │ ├─ JPN:右ぱんつ紐２先 / GBR:right pants string/strap２ destination
│ │ │ │ └─ JPN:スカートセンター先 / GBR:skirt centerー destination
│ │ │ ├─ JPN:左足 / GBR:left foot
│ │ │ │ ├─ JPN:左ひざ / GBR:left knee
│ │ │ │ │ ├─ JPN:左足首 / GBR:left foot head/first
│ │ │ │ │ │ ├─ JPN:左つま先 / GBR:left toe
│ │ │ ├─ JPN:右足 / GBR:right foot
│ │ │ │ ├─ JPN:右ひざ / GBR:right knee
│ │ │ │ │ ├─ JPN:右足首 / GBR:right foot head/first
│ │ │ │ │ │ ├─ JPN:右つま先 / GBR:right toe
│ │ │ ├─ JPN:下半身先 / GBR:lower body/bust destination
│ │ └─ JPN:グルーブ先 / GBR:groove destination
│ └─ JPN:センター先 / GBR:centerー destination
├─ JPN:左足ＩＫ / GBR:left footＩＫ
│ ├─ JPN:左つま先ＩＫ / GBR:left toeＩＫ
│ │ └─ JPN:左つま先ＩＫ先 / GBR:left toeＩＫ destination
│ └─ JPN:左足ＩＫ先 / GBR:left footＩＫ destination
└─ JPN:右足ＩＫ / GBR:right footＩＫ
  ├─ JPN:右つま先ＩＫ / GBR:right toeＩＫ
  │ └─ JPN:右つま先ＩＫ先 / GBR:right toeＩＫ destination
  └─ JPN:右足ＩＫ先 / GBR:right footＩＫ destination
*/
    
/* XNA MODEL
root ground
└─ root hips
  ├─ pelvis
  │ ├─ leg left thigh
  │ │ ├─ leg left knee
  │ │ │ ├─ leg left ankle
  │ │ │ │ └─ leg left toes
  │ │ │ └─ leg left knee ctr
  │ │ ├─ leg left thigh ctr
  │ │ └─ leg left thigh ctr2
  │ ├─ leg right thigh
  │ │ ├─ leg right knee
  │ │ │ ├─ leg right ankle
  │ │ │ │ └─ leg right toes
  │ │ │ └─ leg right knee ctr
  │ │ ├─ leg right thigh ctr
  │ │ └─ leg right thigh ctr2
  │ ├─ leg left butt ctr
  │ └─ leg right butt ctr
  └─ spine lower
    └─ spine upper
      ├─ head neck lower
      │ └─ head neck upper
      │   ├─ unusedfaceroot
      │   │ ├─ head eyebrow middle
      │   │ ├─ head lip upper middle
      │   │ ├─ head nostril middle
      │   │ ├─ head jaw
      │   │ │ ├─ head lip lower middle
      │   │ │ ├─ head tongue 1
      │   │ │ │ └─ head tongue 2
      │   │ │ ├─ head lip lower left
      │   │ │ └─ head lip lower right
      │   │ ├─ head cheek left 2
      │   │ ├─ head cheek left 1
      │   │ ├─ head cheek left 3
      │   │ ├─ head eyeball left
      │   │ ├─ head eyelid left lower a
      │   │ ├─ head eyelid left lower b
      │   │ ├─ head eyebrow left 1
      │   │ ├─ head eyebrow left 2
      │   │ ├─ head eyebrow left 3
      │   │ ├─ head eyelid left upper
      │   │ ├─ forehead eyebrow left
      │   │ ├─ head mouth corner left
      │   │ ├─ head lip upper left
      │   │ ├─ head nostril left 1
      │   │ ├─ head nostril left 2
      │   │ ├─ head nostril left 3
      │   │ ├─ head nostril left 4
      │   │ ├─ head cheek right 2
      │   │ ├─ head cheek right 1
      │   │ ├─ head cheek right 3
      │   │ ├─ head eyeball right
      │   │ ├─ head eyelid right lower a
      │   │ ├─ head eyelid right lower b
      │   │ ├─ head eyebrow right 1
      │   │ ├─ head eyebrow right 2
      │   │ ├─ head eyebrow right 3
      │   │ ├─ head eyelid right upper
      │   │ ├─ forehead eyebrow right
      │   │ ├─ head mouth corner right
      │   │ ├─ head lip upper right
      │   │ ├─ head nostril right 1
      │   │ ├─ head nostril right 2
      │   │ ├─ head nostril right 3
      │   │ └─ head nostril right 4
      │   └─ hair ponytail root
      ├─ arm left shoulder 1
      │ └─ arm left shoulder 2
      │   ├─ arm left elbow
      │   │ ├─ unusedhandlroot
      │   │ │ └─ arm left wrist
      │   │ │   ├─ arm left finger 2a
      │   │ │   │ └─ arm left finger 2b
      │   │ │   │   └─ arm left finger 2c
      │   │ │   ├─ arm left finger 5 base
      │   │ │   │ ├─ arm left finger 5a
      │   │ │   │ │ └─ arm left finger 5b
      │   │ │   │ │   └─ arm left finger 5c
      │   │ │   │ └─ arm left finger 4a
      │   │ │   │   └─ arm left finger 4b
      │   │ │   │     └─ arm left finger 4c
      │   │ │   ├─ arm left finger 3a
      │   │ │   │ └─ arm left finger 3b
      │   │ │   │   └─ arm left finger 3c
      │   │ │   └─ arm left finger 1a
      │   │ │     └─ arm left finger 1b
      │   │ │       └─ arm left finger 1c
      │   │ ├─ arm left elbow ctr
      │   │ ├─ arm left elbow ctr2
      │   │ └─ arm left wrist ctr
      │   ├─ arm left shoulder 2 ctr
      │   └─ arm left shoulder 2 ctr2
      ├─ arm right shoulder 1
      │ └─ arm right shoulder 2
      │   ├─ arm right elbow
      │   │ ├─ unusedhanrroot
      │   │ │ └─ arm right wrist
      │   │ │   ├─ arm right finger 2a
      │   │ │   │ └─ arm right finger 2b
      │   │ │   │   └─ arm right finger 2c
      │   │ │   ├─ arm right finger 5 base
      │   │ │   │ ├─ arm right finger 5a
      │   │ │   │ │ └─ arm right finger 5b
      │   │ │   │ │   └─ arm right finger 5c
      │   │ │   │ └─ arm right finger 4a
      │   │ │   │   └─ arm right finger 4b
      │   │ │   │     └─ arm right finger 4c
      │   │ │   ├─ arm right finger 3a
      │   │ │   │ └─ arm right finger 3b
      │   │ │   │   └─ arm right finger 3c
      │   │ │   └─ arm right finger 1a
      │   │ │     └─ arm right finger 1b
      │   │ │       └─ arm right finger 1c
      │   │ ├─ arm right elbow ctr
      │   │ ├─ arm right elbow ctr2
      │   │ └─ arm right wrist ctr
      │   ├─ arm right shoulder 2 ctr
      │   └─ arm right shoulder 2 ctr2
      ├─ breast left base
      │ └─ breast left tip
      │   ├─ breast left 1
      │   ├─ breast left 2
      │   ├─ breast left 3
      │   ├─ breast left 4
      │   └─ breast left 5
      └─ breast right base
        └─ breast right tip
          ├─ breast right 1
          ├─ breast right 2
          ├─ breast right 3
          ├─ breast right 4
          └─ breast right 5
*/
    
    
/* XNA MODEL
root ground
└─ unusedbone
  ├─ unusedbone
  │ └─ root hips
  │   ├─ spine lower
  │   │ ├─ spine upper
  │   │ │ ├─ head neck lower
  │   │ │ │ ├─ head neck upper
  │   │ │ │ │ └─ unusedbone
  │   │ │ │ │   ├─ head eye right
  │   │ │ │ │   │ ├─ unusedbone
  │   │ │ │ │   │ ├─ head eye right highlights
  │   │ │ │ │   │ │ └─ misc eyeball right gloss 1
  │   │ │ │ │   │ │   └─ misc eyeball right gloss 2
  │   │ │ │ │   ├─ head eye left
  │   │ │ │ │   │ ├─ head eye left highlights
  │   │ │ │ │   │ │ └─ misc eyeball left gloss 1
  │   │ │ │ │   │ │   └─ misc eyeball left gloss 2
  │   │ │ │ │   ├─ unusedbone
  │   │ │ │ │   │ ├─ head hair fringe 1
  │   │ │ │ │   │ │ └─ head hair fringe 2
  │   │ │ │ │   │ ├─ head hair top 1
  │   │ │ │ │   │ │ └─ head hair top 2
  │   │ │ │ │   │ │   └─ head hair top 3
  │   │ │ │ │   │ ├─ head hair twintail right 1
  │   │ │ │ │   │ │ └─ head hair twintail right 2
  │   │ │ │ │   │ ├─ head hair twintail left 1
  │   │ │ │ │   │ │ └─ head hair twintail left 2
  │   │ │ │ │   │ ├─ head hair twintail right 3
  │   │ │ │ │   │ │ ├─ head hair twintail right 4
  │   │ │ │ │   │ │ │ └─ head hair twintail right 5
  │   │ │ │ │   │ │ │   └─ head hair twintail right 6
  │   │ │ │ │   │ ├─ head hair twintail left 3
  │   │ │ │ │   │ │ ├─ head hair twintail left 4
  │   │ │ │ │   │ │ │ └─ head hair twintail left 5
  │   │ │ │ │   │ │ │   └─ head hair twintail left 6
  │   │ │ │ ├─ accessory pendant
  │   │ │ ├─ arm right shoulder 1
  │   │ │ │ └─ arm right shoulder 2
  │   │ │ │   ├─ arm right elbow
  │   │ │ │   │ ├─ arm right wrist adjuster
  │   │ │ │   │ │ └─ arm right wrist
  │   │ │ │   │ │   ├─ arm right finger 1a
  │   │ │ │   │ │   │ └─ arm right finger 1b
  │   │ │ │   │ │   │   └─ arm right finger 1c
  │   │ │ │   │ │   ├─ arm right finger 2a
  │   │ │ │   │ │   │ └─ arm right finger 2b
  │   │ │ │   │ │   │   └─ arm right finger 2c
  │   │ │ │   │ │   ├─ arm right finger 3a
  │   │ │ │   │ │   │ └─ arm right finger 3b
  │   │ │ │   │ │   │   └─ arm right finger 3c
  │   │ │ │   │ │   ├─ arm right finger 4a
  │   │ │ │   │ │   │ └─ arm right finger 4b
  │   │ │ │   │ │   │   └─ arm right finger 4c
  │   │ │ │   │ │   ├─ arm right finger 5a
  │   │ │ │   │ │   │ └─ arm right finger 5b
  │   │ │ │   │ │   │   └─ arm right finger 5c
  │   │ │ │   ├─ arm right shoulder adjuster
  │   │ │ │   ├─ arm right elbow adjuster
  │   │ │ ├─ arm left shoulder 1
  │   │ │ │ └─ arm left shoulder 2
  │   │ │ │   ├─ arm left elbow
  │   │ │ │   │ ├─ arm left wrist adjuster
  │   │ │ │   │ │ └─ arm left wrist
  │   │ │ │   │ │   ├─ arm left finger 5a
  │   │ │ │   │ │   │ └─ arm left finger 5b
  │   │ │ │   │ │   │   └─ arm left finger 5c
  │   │ │ │   │ │   ├─ arm left finger 4a
  │   │ │ │   │ │   │ └─ arm left finger 4b
  │   │ │ │   │ │   │   └─ arm left finger 4c
  │   │ │ │   │ │   ├─ arm left finger 3a
  │   │ │ │   │ │   │ └─ arm left finger 3b
  │   │ │ │   │ │   │   └─ arm left finger 3c
  │   │ │ │   │ │   ├─ arm left finger 2a
  │   │ │ │   │ │   │ └─ arm left finger 2b
  │   │ │ │   │ │   │   └─ arm left finger 2c
  │   │ │ │   │ │   ├─ arm left finger 1a
  │   │ │ │   │ │   │ └─ arm left finger 1b
  │   │ │ │   │ │   │   └─ arm left finger 1c
  │   │ │ │   ├─ arm left shoulder adjuster
  │   │ │ │   ├─ arm left elbow adjuster
  │   │ │ ├─ breast left 1
  │   │ │ │ ├─ breast left 2
  │   │ │ │ │ └─ breast left 3
  │   │ │ ├─ breast right 1
  │   │ │ │ ├─ breast right 2
  │   │ │ │ │ └─ breast right 3
  │   └─ pelvis
  │     ├─ leg left buttock 1
  │     │ └─ leg left buttock 2
  │     ├─ leg right buttock 1
  │     │ └─ leg right buttock 2
  │     ├─ leg left thigh
  │     │ ├─ leg left knee adjuster
  │     │ ├─ leg left thigh adjuster
  │     │ ├─ leg left knee
  │     │ │ ├─ leg left ankle
  │     │ │ │ └─ leg left foot
  │     │ │ │   └─ leg left toes
  │     ├─ leg right thigh
  │     │ ├─ leg right knee adjuster
  │     │ ├─ leg right thigh adjuster
  │     │ ├─ leg right knee
  │     │ │ ├─ leg right ankle
  │     │ │ │ └─ leg right foot
  │     │ │ │   └─ leg right toes
  │     ├─ cloth panty ribbon left 1
  │     │ └─ cloth panty ribbon left 2
  │     ├─ cloth panty ribbon right 1
  │     │ └─ cloth panty ribbon right 2
*/
    
    
}
