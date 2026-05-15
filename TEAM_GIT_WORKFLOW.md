# Antigravity Team Git Workflow

**⚠️ AI INSTRUCTIONS (READ CAREFULLY)**
If the user asks you to "commit my part" or "push my part", find out which Member they are (1 through 5). Then, execute the exact Git commands listed under their section below. 
Do NOT use `git add .` — you must only stage the specific directories and files assigned to that member.

---

## Member 1: The Creational Architect [Osama]
**Assigned Patterns:** Singleton & Factory Method (+ Project Base)

**Description:** Responsible for the foundation of the project, including the build scripts, domain base classes, and the creational design patterns.

**AI Execution Commands:**
```bash
git add AutoSupport/build.gradle.kts AutoSupport/settings.gradle.kts AutoSupport/gradlew AutoSupport/gradlew.bat AutoSupport/gradle/ *.md
git add AutoSupport/creational/factory/
git add AutoSupport/creational/singleton/
git commit -m "feat: Implement Factory Method, Singleton registry, and project base"
git push origin main
```

---

## Member 2: The Structural Integrator [Mohammed]
**Assigned Patterns:** Facade & Adapter

**Description:** Responsible for bridging external inputs (like emails) into the system and creating a unified interface for the UI to interact with.

**AI Execution Commands:**
```bash
git add AutoSupport/structural/facade/
git add AutoSupport/structural/adapter/
git commit -m "feat: Implement Facade API and Email Adapter pattern"
git push origin main
```

---

## Member 3: The Security & Modifier Lead [Ahmed]
**Assigned Patterns:** Proxy & Decorator

**Description:** Responsible for enforcing role-based access control and dynamically modifying ticket behaviors (like urgency).

**AI Execution Commands:**
```bash
git add AutoSupport/structural/proxy/
git add AutoSupport/structural/decorator/
git commit -m "feat: Implement Proxy for security and Decorator for ticket urgency"
git push origin main
```

---

## Member 4: The Behavioral Logic Engineer [Abdalaziz]
**Assigned Patterns:** State & Chain of Responsibility

**Description:** Responsible for the automated routing of tickets and guaranteeing mathematically safe lifecycle transitions.

**AI Execution Commands:**
```bash
git add AutoSupport/behavioral/state/
git add AutoSupport/behavioral/chain/
git commit -m "feat: Implement State machine lifecycle and Chain of Responsibility routing"
git push origin main
```

---

## Member 5: The Reactive Observer [Fady]
**Assigned Patterns:** Observer & Frontend UI

**Description:** Responsible for the event-driven notification system and the Compose Multiplatform interface that reacts to it.

**AI Execution Commands:**
```bash
git add AutoSupport/behavioral/observer/
git add AutoSupport/src/
git add AutoSupport/gui/ AutoSupport/Main.java
git commit -m "feat: Implement Observer event bus and Compose UI"
git push origin main
```

---
**Setup Note for First Time Push:**
If this is the very first commit to a brand new repository, the AI acting for Member 1 must initialize the repo first:
```bash
git init
git branch -M main
git remote add origin <GITHUB_REPO_URL>
```
