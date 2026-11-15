package com.adoptme.app.data.local

import com.adoptme.app.R
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.AdoptionApplication
import com.adoptme.app.data.model.Clinic
import com.adoptme.app.data.model.DonationOption
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.LostFoundPost
import com.adoptme.app.data.model.LostFoundType
import com.adoptme.app.data.model.OnboardingPage
import com.adoptme.app.data.model.Shelter

object SampleData {

    val onboardingPages = listOf(
        OnboardingPage(
            title = "Find your soulmate",
            description = "Browse adorable cats and dogs waiting for a loving forever home.",
            imageRes = R.drawable.onboarding_cat
        ),
        OnboardingPage(
            title = "Adopt with heart",
            description = "Simple adoption applications so you can welcome a new friend faster.",
            imageRes = R.drawable.onboarding_dog
        ),
        OnboardingPage(
            title = "Love starts here",
            description = "Save favorites, visit shelters nearby, and change a life today.",
            imageRes = R.drawable.onboarding_friends
        )
    )

    val animals = listOf(
        Animal(
            id = "1",
            name = "Luna",
            type = AnimalType.CAT,
            breed = "British Shorthair",
            age = "2 years",
            gender = Gender.FEMALE,
            healthStatus = HealthStatus.HEALTHY,
            description = "Luna is a calm, affectionate queen who loves sunny windows and gentle pets. She gets along with quiet homes and soft blankets.",
            imageUrl = "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=800",
            location = "Almaty Central Shelter",
            size = "Medium"
        ),
        Animal(
            id = "2",
            name = "Buddy",
            type = AnimalType.DOG,
            breed = "Golden Retriever",
            age = "3 years",
            gender = Gender.MALE,
            healthStatus = HealthStatus.HEALTHY,
            description = "Buddy is playful, loyal, and great with kids. He enjoys long walks and learning new tricks.",
            imageUrl = "https://images.unsplash.com/photo-1552053831-71594a27632d?w=800",
            location = "Happy Paws Shelter",
            size = "Large"
        ),
        Animal(
            id = "3",
            name = "Milo",
            type = AnimalType.CAT,
            breed = "Tabby",
            age = "1 year",
            gender = Gender.MALE,
            healthStatus = HealthStatus.HEALTHY,
            description = "Curious explorer with soft orange fur. Milo loves toys and afternoon naps on your laptop.",
            imageUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800",
            location = "Almaty Central Shelter",
            size = "Small"
        ),
        Animal(
            id = "4",
            name = "Bella",
            type = AnimalType.DOG,
            breed = "Beagle",
            age = "4 years",
            gender = Gender.FEMALE,
            healthStatus = HealthStatus.RECOVERING,
            description = "Sweet Bella is recovering beautifully and looking for a calm, caring family. She adores sniff walks.",
            imageUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=800",
            location = "Kind Hearts Rescue",
            size = "Medium",
            vaccinated = true,
            sterilized = true
        ),
        Animal(
            id = "5",
            name = "Nala",
            type = AnimalType.CAT,
            breed = "Siamese Mix",
            age = "8 months",
            gender = Gender.FEMALE,
            healthStatus = HealthStatus.HEALTHY,
            description = "Elegant and chatty. Nala wants a human who talks back and shares cozy evenings.",
            imageUrl = "https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?w=800",
            location = "Happy Paws Shelter",
            size = "Small"
        ),
        Animal(
            id = "6",
            name = "Max",
            type = AnimalType.DOG,
            breed = "Husky Mix",
            age = "2 years",
            gender = Gender.MALE,
            healthStatus = HealthStatus.HEALTHY,
            description = "Energetic adventurer who needs daily activity and lots of love. Max is crate-trained and friendly.",
            imageUrl = "https://images.unsplash.com/photo-1605568427561-40dd23c2acea?w=800",
            location = "Mountain Trail Rescue",
            size = "Large"
        ),
        Animal(
            id = "7",
            name = "Cleo",
            type = AnimalType.CAT,
            breed = "Persian",
            age = "5 years",
            gender = Gender.FEMALE,
            healthStatus = HealthStatus.NEEDS_CARE,
            description = "Gentle fluffball who needs special grooming care. Cleo rewards patience with endless purrs.",
            imageUrl = "https://images.unsplash.com/photo-1573865526739-10c1dd7a7de0?w=800",
            location = "Kind Hearts Rescue",
            size = "Medium"
        ),
        Animal(
            id = "8",
            name = "Charlie",
            type = AnimalType.DOG,
            breed = "Corgi Mix",
            age = "1.5 years",
            gender = Gender.MALE,
            healthStatus = HealthStatus.HEALTHY,
            description = "Short legs, big personality. Charlie is the life of every park visit.",
            imageUrl = "https://images.unsplash.com/photo-1612536057832-2ff7ebd6c4f6?w=800",
            location = "Happy Paws Shelter",
            size = "Small"
        )
    )

    val shelters = listOf(
        Shelter("s1", "Almaty Central Shelter", "Abay Ave 150, Almaty", "+7 727 000 1111", 43.238949, 76.945465, "09:00 – 18:00"),
        Shelter("s2", "Happy Paws Shelter", "Dostyk Ave 42, Almaty", "+7 727 000 2222", 43.256652, 76.928480, "10:00 – 19:00"),
        Shelter("s3", "Kind Hearts Rescue", "Satpayev St 18, Almaty", "+7 727 000 3333", 43.240500, 76.910200, "09:30 – 17:30"),
        Shelter("s4", "Mountain Trail Rescue", "Kok-Tobe Rd 7, Almaty", "+7 727 000 4444", 43.230100, 76.975300, "08:00 – 16:00")
    )

    val clinics = listOf(
        Clinic("c1", "VetCare Plus", "Tole Bi 88, Almaty", "+7 727 111 1001", "General · Surgery · Vaccines", 4.8f),
        Clinic("c2", "Paw & Claw Clinic", "Nazarbayev Ave 210", "+7 727 111 1002", "Emergency · Dentistry", 4.6f),
        Clinic("c3", "SoftPaws Veterinary", "Rozybakiev St 55", "+7 727 111 1003", "Cats & Dogs · Diagnostics", 4.9f)
    )

    val lostFound = listOf(
        LostFoundPost(
            "lf1", LostFoundType.LOST, "Simba", "Cat",
            "Orange tabby with blue collar, last seen near Abay park.",
            "Abay Park, Almaty", "+7 700 111 2233",
            "https://images.unsplash.com/photo-1495366691023-cc4ead609f61?w=600",
            "2 days ago"
        ),
        LostFoundPost(
            "lf2", LostFoundType.FOUND, "Unknown Puppy", "Dog",
            "Small brown puppy found near Dostyk metro. Looking for owner.",
            "Dostyk Metro", "+7 700 222 3344",
            "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=600",
            "Yesterday"
        ),
        LostFoundPost(
            "lf3", LostFoundType.LOST, "Muffin", "Cat",
            "White fluffy cat, microchipped, responds to Muffin.",
            "Medeu District", "+7 700 333 4455",
            "https://images.unsplash.com/photo-1526336024174-e58f5cdd8e13?w=600",
            "5 days ago"
        )
    )

    val donations = listOf(
        DonationOption("d1", "Feed a pet", "Provide quality food for a week", "$15", "food"),
        DonationOption("d2", "Medical care", "Cover vaccines and checkups", "$40", "medical"),
        DonationOption("d3", "Shelter support", "Help with bedding and toys", "$25", "shelter"),
        DonationOption("d4", "Volunteer", "Join weekend walking & care shifts", "Free", "volunteer")
    )

    val sampleApplications = listOf(
        AdoptionApplication(
            id = "a1",
            animalId = "1",
            animalName = "Luna",
            applicantName = "Demo User",
            email = "demo@adoptme.app",
            phone = "+7 700 000 0000",
            address = "Almaty",
            housingType = "Apartment",
            hasOtherPets = false,
            experience = "Previous cat owner",
            reason = "Looking for a calm companion",
            status = ApplicationStatus.UNDER_REVIEW
        )
    )

    val notifications = listOf(
        AppNotification(
            "n1",
            "Application update",
            "Your application for Luna is under review.",
            "10 min ago",
            false,
            "a1"
        ),
        AppNotification(
            "n2",
            "New match nearby",
            "Buddy at Happy Paws matches your preferences.",
            "1 hour ago",
            false
        ),
        AppNotification(
            "n3",
            "Thank you!",
            "Your donation helped feed 12 shelter pets this week.",
            "Yesterday",
            true
        )
    )
}
