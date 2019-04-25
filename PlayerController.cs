using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class PlayerController : MonoBehaviour {
    public float speed;
    public float jumpspeed;
    public float dodgespeed = 15;
    public float attackrate =0.5F;
    public float nextattack = 0.5F;
    public static bool paused = false;
    public Slider health;
    public int starthealth = 100;
    public int currenthealth = 50;
    public float dodgedelay=2;
    public float nextdodge=0;
    GameObject sword;
    Ground gd;
    int swordpos;

    void Start()
    {   
        GetComponent<Rigidbody>().constraints = RigidbodyConstraints.FreezeRotationX | RigidbodyConstraints.FreezeRotationZ | RigidbodyConstraints.FreezeRotationY;
        sword = GameObject.FindGameObjectWithTag("Sword");
        swordpos = 1;
        gd = GameObject.FindGameObjectWithTag("Ground").GetComponent<Ground>();
        health.value = currenthealth;
        
    }

    //This is where jumping, shooting and dodging are checked and performed though other functions.
    void Update(){
        Rigidbody rigid = GameObject.FindGameObjectWithTag("Player").GetComponent<Rigidbody>();
        if (Input.GetKeyDown("space") && gd.grounded){
            rigid.velocity = rigid.velocity + Vector3.up*jumpspeed;
        }

        if (Input.GetMouseButton(0) && Time.time >nextattack)
        {
            nextattack = Time.time + attackrate;
            Swing();

        }

        if (Input.GetMouseButton(1))
        {
            if (Time.time > nextdodge)
            {
                Dodge();
                nextdodge = Time.time+dodgedelay;
            }

        }
        if (currenthealth <= 0)
        {
            //Change
            Application.Quit();
        }
        if (Input.GetKeyDown("p"))
        {
            if (Time.timeScale != 0)
            {
                Time.timeScale = 0;
                paused = true;
            }
            else
            {
                Time.timeScale = 1;
                paused = false;
            }
        }
    }

    //gets input from keyboard and adds force to move player
    void FixedUpdate(){
        float horAxis = Input.GetAxis("Horizontal");
        float verAxis = Input.GetAxis("Vertical");
        Vector3 controlmovement = new Vector3(horAxis, 0.0f, verAxis);
        GetComponent<Rigidbody>().AddRelativeForce(controlmovement * speed * Time.deltaTime);
        
    }

    //Moves player sword from the left to the right.
    void Swing()
    {
        if (swordpos == 1)
        {
            sword.transform.localPosition = Vector3.Slerp(sword.transform.localPosition, new Vector3(-1, 0, 2), 0.75f);
            swordpos = 0;
        } else
        {
            sword.transform.localPosition = Vector3.Slerp(sword.transform.localPosition, new Vector3(1, 0, 2), 0.75f);
            swordpos = 1;
        }
    }

    //translates the player model backwards
    void Dodge()
    {
        GetComponent<Rigidbody>().velocity = new Vector3(0, 0, 0);
        transform.Translate(Vector3.back * dodgespeed);
    }

    //adds health. Executed by HealthPickUpScript.cs
    public void healthpickup() {
        if (currenthealth >= 75)
        {
            currenthealth = 100;
        } else
        {
            currenthealth += 25;
        }
        health.value = currenthealth;
    }

    //If player collides with bolt, they will lose health.
    void OnCollisionEnter(Collision other)
    {
        if (other.gameObject.tag == "Bolt")
        {
            currenthealth -= 10;
            if (currenthealth <= 0)
            {
                Application.LoadLevel(1);
                Destroy(gameObject);
            }
            health.value = currenthealth;
        }
    }

}
