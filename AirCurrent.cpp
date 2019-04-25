// Fill out your copyright notice in the Description page of Project Settings.

#include "AirCurrent.h"
#include "Components/BoxComponent.h"
#include "PlayerControl.h"
#include "PaperFlipbookComponent.h"


// Sets default values
AAirCurrent::AAirCurrent()
{
 	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;
	Root = CreateDefaultSubobject<USceneComponent>(TEXT("Root"));
	RootComponent = Root;
	CurrentCube = CreateDefaultSubobject<UBoxComponent>(TEXT("CurrentCube"));
	CurrentCube->SetupAttachment(RootComponent);
	CurrentCube->bGenerateOverlapEvents = true;
	CurrentCube->OnComponentBeginOverlap.AddDynamic(this, &AAirCurrent::OnPlayerOverlap);
	CurrentCube->OnComponentEndOverlap.AddDynamic(this, &AAirCurrent::OnPlayerOverlapEnd);

	inCurrent = false;

	Sprite = CreateDefaultSubobject<UPaperFlipbookComponent>(TEXT("Sprite"));
	Sprite->SetupAttachment(RootComponent);
}

// Called when the game starts or when spawned
void AAirCurrent::BeginPlay()
{
	Super::BeginPlay();
	
}

// Called every frame
void AAirCurrent::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);
	if (inCurrent && Player!=NULL) {
		Player->AddMovementInput(FVector(0.0f,0.0f,1.0f));
	}
}

void AAirCurrent::OnPlayerOverlap(UPrimitiveComponent * OverlappedComp, AActor * OtherActor, UPrimitiveComponent * OtherComp, int32 OtherBodyIndex, bool bFromSweep, const FHitResult & SweepResult)
{
	if (Player != nullptr) {
		UE_LOG(LogTemp, Warning, TEXT("Overlap Current 2"));
		inCurrent = true;
		Player = Cast<APlayerControl>(OtherActor);
		Player->ChangeMovementMode(inCurrent);
	}
	if (OtherActor->IsA(APlayerControl::StaticClass())) {
		UE_LOG(LogTemp, Warning, TEXT("Overlap Current 1"));
		inCurrent = true;
		Player = Cast<APlayerControl>(OtherActor);
		Player->ChangeMovementMode(inCurrent);
	}
}

void AAirCurrent::OnPlayerOverlapEnd(UPrimitiveComponent * OverlappedComp, AActor * OtherActor, UPrimitiveComponent * OtherComp, int32 OtherBodyIndex)
{
	UE_LOG(LogTemp, Warning, TEXT("Overlap End Current"));
	inCurrent = false;
	Player->ChangeMovementMode(inCurrent);
}

