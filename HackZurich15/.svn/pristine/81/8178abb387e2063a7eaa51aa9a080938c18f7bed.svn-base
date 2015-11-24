using MongoRepository;
using System;
using System.Runtime.Serialization;

namespace PhoneWars.Api.Models
{
    [DataContract]
    public class History : Entity
    {
        [DataMember]
        public DateTime DateUtc { get; set; }

        [DataMember]
        public string WinningPlayerId { get; set; }

        [DataMember]
        public string LoosingPlayerId { get; set; }

        [DataMember]
        public byte[] Image { get; set; }
    }
}