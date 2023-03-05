/**
 * 
 */
package com.valleau.pierre.irc.server.services;


/**
 * @author Pierre Valleau
 *
 *
 *https://fr.wikipedia.org/wiki/Services_IRC
 Les services IRC sont une série d'outils mis à la disposition des utilisateurs d'un réseau IRC. Ils permettent aux utilisateurs de s'authentifier et de gérer leurs salons. Ils sont aussi d'une grande aide aux administrateurs, les IrcOps, pour gérer le réseau.
Généralités

Un certain nombre d'outils sont apparus pour lutter contre différentes nuisances sur IRC, autant du côté client (eggdrops, clients scriptés contre le spam/flood/etc) que du côté serveur. Les services IRC permettent principalement une authentification centralisée et une gestion plus fine des droits autant pour les utilisateurs d'un salon que les administrateurs du réseau.

Une fois inscrit, l'utilisateur pourra enregistrer un salon, et en devenir ainsi le propriétaire (owner). Les services ajoutent aux modes utilisateurs proposés par le serveur IRC (+o, +v) un système de niveaux et d'accès aux commandes. Le propriétaire peut ajouter n'importe quel utilisateur enregistré dans la liste d'accès du salon, c’est-à-dire la liste des utilisateurs ayant un privilège quelconque sur le salon (même celui d'être banni). Chaque commande pour un salon est accompagnée aussi du niveau minimum nécessaire pour l'utiliser, défini par le propriétaire. Cela marche exactement pareil pour les IrcOps, le ou les responsables pouvant définir une hiérarchie adaptée à leurs besoins, et ajouter un utilisateur ou changer son niveau dans la hiérarchie sans avoir besoin d'écrire dans un fichier de configuration du serveur. Ainsi Anope divise la hiérarchie des administrateurs comme suit : Opérateur des services, Administrateur des services, Administrateur du réseau. Cette hiérarchie dans les services est complémentaire a la hiérarchie utilisée par le serveur IRC. 

  * @todo develop it
  * */
public interface IIrcservice {

	public String help();
	
}
