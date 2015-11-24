using MongoRepository;
using System;
using System.Collections.Generic;
using System.Runtime.Serialization;

namespace PhoneWars.Api.Models
{
    [DataContract]
    public class Player : Entity
    {    
        [DataMember]
        public string Nickname { get; set; }

        [DataMember]
        public string HomeAddress { get; set; }

        [DataMember]
        public string WorkAddress { get; set; }

        [DataMember]
        public string Phone { get; set; }

        [DataMember]
        public string Email { get; set; }

        [DataMember]
        public string PasswordHash { get; set; }

        [DataMember]
        public string SecretCode { get; set; }

        [DataMember]
        public byte[] Image { get; set; }

        [DataMember]
        public int Level { get; set; }     

        [DataMember]
        public List<PlayerLocation> LocationHistory { get; set; }

        [DataMember]
        public string HunterId { get; set; }

        [DataMember]
        public string VictimId { get; set; }

        /// <summary>
        /// Date when player started playing (since last death)
        /// </summary>
        [DataMember]
        public DateTime StartedDate { get; set; }

        /// <summary>
        /// Date until when he needs to kill the next victim
        /// </summary>
        [DataMember]
        public DateTime DeadlineDate { get; set; }

        /// <summary>
        /// Player is dead and needs to reset his account to play again
        /// </summary>
        [DataMember]
        public bool IsDead { get; set; }
    }

    public class PlayerLocation
    {
        public DateTime DateUtc { get; set; }

        public double Lat { get; set; }

        public double Lng { get; set; }

    }
}